package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.TokenService
import com.tencent.devops.scm.api.pojo.Oauth2AccessToken
import com.tencent.devops.scm.sdk.gitlab.GitlabOauth2Api
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabOauth2AccessToken

class GitlabTokenService(private val oauth2Api: GitlabOauth2Api) : TokenService {
    override fun authorizationUrl(state: String): String = oauth2Api.authorizationUrl(state)
    override fun callback(code: String): Oauth2AccessToken = convert(oauth2Api.callback(code))
    override fun refresh(refreshToken: String): Oauth2AccessToken = convert(oauth2Api.refresh(refreshToken))

    private fun convert(from: GitlabOauth2AccessToken): Oauth2AccessToken {
        val accessToken = from.accessToken?.takeIf(String::isNotBlank)
            ?: throw IllegalArgumentException("OAuth access_token cannot be blank")
        val tokenType = from.tokenType?.takeIf(String::isNotBlank)
            ?: throw IllegalArgumentException("OAuth token_type cannot be blank")
        return Oauth2AccessToken(
            accessToken = accessToken,
            tokenType = tokenType,
            expiresIn = from.expiresIn ?: 0,
            refreshToken = from.refreshToken.orEmpty(),
            scope = from.scope
        )
    }
}
