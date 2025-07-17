package com.tencent.devops.scm.provider.gitee.simple.auth

import com.gitee.sdk.gitee5j.ApiClient
import com.gitee.sdk.gitee5j.auth.OAuth
import com.tencent.devops.scm.api.pojo.auth.AccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenUserPassScmAuth

class GiteeTokenAuthProviderAdapter(private val apiClient: ApiClient) {

    companion object {
        private const val AUTH_TYPE_OAUTH = "OAuth2"
    }

    fun withAuth(auth: IScmAuth) {
        val oAuth = apiClient.getAuthentication(AUTH_TYPE_OAUTH) as OAuth
        val token = when (auth) {
            is AccessTokenScmAuth -> auth.accessToken
            is PersonalAccessTokenScmAuth -> auth.personalAccessToken
            is TokenUserPassScmAuth -> auth.token
            is TokenSshPrivateKeyScmAuth -> auth.token
            else -> throw UnsupportedOperationException("gitAuth($auth) is not support")
        }
        oAuth.accessToken = token
    }
}
