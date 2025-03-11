package com.tencent.devops.scm.spring.manager;

import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.connector.okhttp3.OkHttpScmConnector;
import com.tencent.devops.scm.spring.properties.HttpClientProperties;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public class OkHttpScmConnectorFactory implements ScmConnectorFactory {

    private final ConcurrentHashMap<String, ScmConnector> connectorCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, HttpClientProperties> propertiesCache = new ConcurrentHashMap<>();

    private final OkHttpMetricsEventListener metricsEventListener;

    public OkHttpScmConnectorFactory(OkHttpMetricsEventListener metricsEventListener) {
        this.metricsEventListener = metricsEventListener;
    }

    @Override
    public ScmConnector create(HttpClientProperties properties) {
        String apiUrl = properties.getApiUrl();
        if (apiUrl == null) {
            throw new IllegalArgumentException("api url is required");
        }

        HttpClientProperties cachedProps = propertiesCache.get(apiUrl);
        if (properties.equals(cachedProps)) {
            return connectorCache.get(apiUrl);
        }
        ScmConnector connector = createConnector(properties);
        connectorCache.put(apiUrl, connector);
        propertiesCache.put(apiUrl, properties);
        return connector;
    }

    private ScmConnector createConnector(HttpClientProperties properties) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (properties.getConnectTimeout() != null) {
            builder.connectTimeout(properties.getConnectTimeout(), TimeUnit.SECONDS);
        }
        if (properties.getReadTimeout() != null) {
            builder.readTimeout(properties.getReadTimeout(), TimeUnit.SECONDS);
        }
        if (properties.getWriteTimeout() != null) {
            builder.writeTimeout(properties.getWriteTimeout(), TimeUnit.SECONDS);
        }
        if (metricsEventListener != null) {
            builder.eventListener(metricsEventListener);
        }
        return new OkHttpScmConnector(builder.build());
    }
}
