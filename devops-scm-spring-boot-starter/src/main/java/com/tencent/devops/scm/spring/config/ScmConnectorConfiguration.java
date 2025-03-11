package com.tencent.devops.scm.spring.config;

import static com.tencent.devops.scm.sdk.common.constants.ScmSdkConstants.SCM_REPO_ID_HEADER;

import com.tencent.devops.scm.spring.manager.OkHttpScmConnectorFactory;
import com.tencent.devops.scm.spring.manager.ScmConnectorFactory;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(name = "org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration")
public class ScmConnectorConfiguration {

    @Bean
    @ConditionalOnMissingBean(ScmConnectorFactory.class)
    public ScmConnectorFactory connectorFactory(
            @Autowired(required = false) OkHttpMetricsEventListener metricsEventListener) {
        return new OkHttpScmConnectorFactory(metricsEventListener);
    }

    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnProperty(prefix = "management.metrics", name = "enabled", matchIfMissing = true)
    public OkHttpMetricsEventListener okHttpMetricsEventListener(MeterRegistry meterRegistry) {
        return OkHttpMetricsEventListener.builder(meterRegistry, "devops.scm.requests")
                .tag((request, response) -> {
                    String projectIdOrPath = request.header(SCM_REPO_ID_HEADER);
                    return Tag.of("scm_repo_id", StringUtils.defaultIfBlank(projectIdOrPath, ""));
                })
                .build();
    }
}
