package com.tencent.devops.scm.spring.config

import com.tencent.devops.scm.spring.manager.BkCodeScmProviderFactory
import com.tencent.devops.scm.spring.manager.GiteeScmProviderFactory
import com.tencent.devops.scm.spring.manager.ScmConnectorFactory
import com.tencent.devops.scm.spring.manager.ScmProviderFactory
import com.tencent.devops.scm.spring.manager.ScmProviderManager
import com.tencent.devops.scm.spring.manager.TGitScmProviderFactory
import com.tencent.devops.scm.spring.manager.TSvnScmProviderFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ScmProviderConfiguration {

    @Bean
    @ConditionalOnMissingBean(ScmProviderManager::class)
    fun scmProviderManager(providerFactories: List<ScmProviderFactory>) = ScmProviderManager(providerFactories)

    @Bean
    @ConditionalOnMissingBean(TGitScmProviderFactory::class)
    fun tGitScmProviderFactory(connectorFactory: ScmConnectorFactory) = TGitScmProviderFactory(connectorFactory)

    @Bean
    @ConditionalOnMissingBean(TSvnScmProviderFactory::class)
    fun tSvnScmProviderFactory(connectorFactory: ScmConnectorFactory) = TSvnScmProviderFactory(connectorFactory)

    @Bean
    @ConditionalOnMissingBean(GiteeScmProviderFactory::class)
    fun giteeScmProviderFactory(connectorFactory: ScmConnectorFactory) = GiteeScmProviderFactory(connectorFactory)

    @Bean
    @ConditionalOnMissingBean(BkCodeScmProviderFactory::class)
    fun bkCodeScmProviderFactory(connectorFactory: ScmConnectorFactory) = BkCodeScmProviderFactory(connectorFactory)
}
