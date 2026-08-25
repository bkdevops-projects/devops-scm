package com.tencent.devops.scm.provider.git.gitlab.auth

import com.tencent.devops.scm.api.pojo.auth.AccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth
import com.tencent.devops.scm.sdk.gitlab.auth.GitlabAuthProvider

object GitlabAuthProviderFactory {
    private val adapters = listOf(GitlabOauthAuthProviderAdapter(), GitlabPrivateTokenAuthProviderAdapter())

    fun support(auth: IScmAuth): Boolean = auth is AccessTokenScmAuth ||
        auth is PersonalAccessTokenScmAuth || auth is TokenSshPrivateKeyScmAuth

    fun create(auth: IScmAuth): GitlabAuthProvider {
        if (!support(auth)) {
            throw UnsupportedOperationException("unsupported GitLab auth type: ${auth.javaClass.name}")
        }
        return adapters.first { it.support(auth) }.get(auth)
    }
}
