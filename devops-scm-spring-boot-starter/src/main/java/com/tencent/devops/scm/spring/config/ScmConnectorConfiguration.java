package com.tencent.devops.scm.spring.config;


import com.tencent.devops.scm.spring.manager.OkHttpScmConnectorFactory;
import com.tencent.devops.scm.spring.manager.ScmConnectorFactory;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
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
        return OkHttpMetricsEventListener.builder(meterRegistry, "devops.scm.requests").build();
    }
}
