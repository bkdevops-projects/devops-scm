package com.tencent.devops.scm.spring.manager

import com.tencent.devops.scm.api.ScmProvider
import com.tencent.devops.scm.api.enums.ScmProviderCodes
import com.tencent.devops.scm.provider.git.gitee.GiteeScmProvider
import com.tencent.devops.scm.sdk.common.GitOauth2ClientProperties
import com.tencent.devops.scm.spring.properties.ScmProviderProperties

class GiteeScmProviderFactory(
    private val connectorFactory: ScmConnectorFactory
) : ScmProviderFactory {

    override fun support(properties: ScmProviderProperties): Boolean {
        return ScmProviderCodes.GITEE.name == properties.providerCode
    }

    override fun build(properties: ScmProviderProperties, tokenApi: Boolean): ScmProvider {
        val httpClientProperties = properties.httpClientProperties!!
        val connector = connectorFactory.create(httpClientProperties)
        val giteeOauth2ClientProperties = getGiteeOauth2ClientProperties(properties, tokenApi)

        return if (giteeOauth2ClientProperties != null) {
            GiteeScmProvider(
                httpClientProperties.apiUrl ?: "",
                connector,
                giteeOauth2ClientProperties
            )
        } else {
            GiteeScmProvider(httpClientProperties.apiUrl ?: "", connector)
        }
    }

    private fun getGiteeOauth2ClientProperties(
        properties: ScmProviderProperties,
        tokenApi: Boolean
    ): GitOauth2ClientProperties? {
        if (!tokenApi || properties.oauth2Enabled != true || properties.oauth2ClientProperties == null) {
            return null
        }

        val oauth2ClientProperties = properties.oauth2ClientProperties!!
        return GitOauth2ClientProperties(
            oauth2ClientProperties.webUrl,
            oauth2ClientProperties.clientId,
            oauth2ClientProperties.clientSecret,
            oauth2ClientProperties.redirectUri
        )
    }
}
