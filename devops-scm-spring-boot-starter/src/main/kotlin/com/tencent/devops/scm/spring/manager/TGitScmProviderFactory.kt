package com.tencent.devops.scm.spring.manager

import com.tencent.devops.scm.api.ScmProvider
import com.tencent.devops.scm.api.enums.ScmProviderCodes
import com.tencent.devops.scm.provider.git.tgit.TGitScmProvider
import com.tencent.devops.scm.sdk.common.GitOauth2ClientProperties
import com.tencent.devops.scm.spring.properties.ScmProviderProperties

class TGitScmProviderFactory(
    private val connectorFactory: ScmConnectorFactory
) : ScmProviderFactory {

    /**
     * 判断是否支持TGIT仓库
     *
     * @param properties 仓库属性
     * @return 是否支持
     */
    override fun support(properties: ScmProviderProperties): Boolean {
        return ScmProviderCodes.TGIT.name == properties.providerCode
    }

    /**
     * 构建TGIT SCM提供者
     *
     * @param properties 仓库属性
     * @param tokenApi 是否是token api请求
     * @return TGIT SCM提供者实例
     */
    override fun build(properties: ScmProviderProperties, tokenApi: Boolean): ScmProvider {
        val httpClientProperties = properties.httpClientProperties!!
        val connector = connectorFactory.create(httpClientProperties)
        val tgitOauth2ClientProperties = getGitOauth2ClientProperties(properties, tokenApi)

        return if (tgitOauth2ClientProperties != null) {
            TGitScmProvider(httpClientProperties.apiUrl ?: "", connector, tgitOauth2ClientProperties)
        } else {
            TGitScmProvider(httpClientProperties.apiUrl ?: "", connector)
        }
    }

    /**
     * 获取TGIT OAuth2客户端配置
     *
     * @param properties 仓库属性
     * @param tokenApi 是否是token api请求
     * @return OAuth2客户端配置，如果不满足条件则返回null
     */
    private fun getGitOauth2ClientProperties(
        properties: ScmProviderProperties,
        tokenApi: Boolean
    ): GitOauth2ClientProperties? {
        if (tokenApi && properties.oauth2Enabled == true && properties.oauth2ClientProperties != null) {
            val oauth2ClientProperties = properties.oauth2ClientProperties!!
            return GitOauth2ClientProperties(
                oauth2ClientProperties.webUrl,
                oauth2ClientProperties.clientId,
                oauth2ClientProperties.clientSecret,
                oauth2ClientProperties.redirectUri
            )
        }
        return null
    }
}