package com.tencent.devops.scm.spring.manager;

import com.tencent.devops.scm.api.ScmProvider;
import com.tencent.devops.scm.api.enums.ScmProviderCodes;
import com.tencent.devops.scm.provider.svn.tsvn.TSvnScmProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.spring.properties.HttpClientProperties;
import com.tencent.devops.scm.spring.properties.ScmProviderProperties;

public class TSvnScmProviderFactory implements ScmProviderFactory {

    private final ScmConnectorFactory connectorFactory;

    public TSvnScmProviderFactory(ScmConnectorFactory connectorFactory) {
        this.connectorFactory = connectorFactory;
    }

    @Override
    public Boolean support(ScmProviderProperties properties) {
        return ScmProviderCodes.TSVN.name().equals(properties.getProviderCode());
    }

    @Override
    public ScmProvider build(ScmProviderProperties properties, boolean tokenApi) {
        HttpClientProperties httpClientProperties = properties.getHttpClientProperties();
        ScmConnector connector = connectorFactory.create(httpClientProperties);
        return new TSvnScmProvider(httpClientProperties.getApiUrl(), connector);
    }
}
