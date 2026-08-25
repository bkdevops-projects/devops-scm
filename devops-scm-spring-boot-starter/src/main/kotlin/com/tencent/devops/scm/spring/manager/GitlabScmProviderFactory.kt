package com.tencent.devops.scm.spring.manager

import com.tencent.devops.scm.api.ScmProvider
import com.tencent.devops.scm.api.enums.ScmProviderCodes
import com.tencent.devops.scm.provider.git.gitlab.GitlabScmProvider
import com.tencent.devops.scm.sdk.common.GitOauth2ClientProperties
import com.tencent.devops.scm.spring.properties.ScmProviderProperties

class GitlabScmProviderFactory(
    private val connectorFactory: ScmConnectorFactory
) : ScmProviderFactory {

    override fun support(properties: ScmProviderProperties): Boolean {
        return ScmProviderCodes.GITLAB.name == properties.providerCode
    }

    override fun build(properties: ScmProviderProperties, tokenApi: Boolean): ScmProvider {
        val httpClientProperties = requireNotNull(properties.httpClientProperties) {
            "httpClientProperties cannot be null"
        }
        val apiUrl = requireNotNull(httpClientProperties.apiUrl?.takeIf { it.isNotBlank() }) {
            "httpClientProperties.apiUrl cannot be blank"
        }
        val connector = connectorFactory.create(httpClientProperties)
        val oauthProperties = oauthProperties(properties, tokenApi)
        return if (oauthProperties == null) {
            GitlabScmProvider(apiUrl, connector)
        } else {
            GitlabScmProvider(apiUrl, connector, oauthProperties)
        }
    }

    private fun oauthProperties(
        properties: ScmProviderProperties,
        tokenApi: Boolean
    ): GitOauth2ClientProperties? {
        if (!tokenApi || properties.oauth2Enabled != true) return null
        val oauth = requireNotNull(properties.oauth2ClientProperties) {
            "oauth2ClientProperties cannot be null when OAuth2 is enabled"
        }
        return GitOauth2ClientProperties(
            oauth.webUrl.required("oauth2ClientProperties.webUrl"),
            oauth.clientId.required("oauth2ClientProperties.clientId"),
            oauth.clientSecret.required("oauth2ClientProperties.clientSecret"),
            oauth.redirectUri.required("oauth2ClientProperties.redirectUri")
        )
    }

    private fun String?.required(name: String): String {
        return requireNotNull(this?.takeIf { it.isNotBlank() }) { "$name cannot be blank" }
    }
}
