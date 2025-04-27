package com.tencent.devops.scm.spring.config;

import com.tencent.devops.scm.spring.manager.GiteeScmProviderFactory;
import com.tencent.devops.scm.spring.manager.ScmConnectorFactory;
import com.tencent.devops.scm.spring.manager.ScmProviderFactory;
import com.tencent.devops.scm.spring.manager.ScmProviderManager;
import com.tencent.devops.scm.spring.manager.TGitScmProviderFactory;
import com.tencent.devops.scm.spring.manager.TSvnScmProviderFactory;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScmProviderConfiguration {
    @Bean
    @ConditionalOnMissingBean(ScmProviderManager.class)
    public ScmProviderManager scmProviderManager(List<ScmProviderFactory> providerFactories) {
        return new ScmProviderManager(providerFactories);
    }

    @Bean
    @ConditionalOnMissingBean(TGitScmProviderFactory.class)
    public TGitScmProviderFactory tGitScmProviderFactory(ScmConnectorFactory connectorFactory) {
        return new TGitScmProviderFactory(connectorFactory);
    }

    @Bean
    @ConditionalOnMissingBean(TSvnScmProviderFactory.class)
    public TSvnScmProviderFactory tSvnScmProviderFactory(ScmConnectorFactory connectorFactory) {
        return new TSvnScmProviderFactory(connectorFactory);
    }

    @Bean
    @ConditionalOnMissingBean(GiteeScmProviderFactory.class)
    public GiteeScmProviderFactory giteeScmProviderFactory(ScmConnectorFactory connectorFactory) {
        return new GiteeScmProviderFactory(connectorFactory);
    }
}
