package com.tencent.devops.scm.provider.git.gitlab.auth

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth
import com.tencent.devops.scm.sdk.gitlab.auth.GitlabAuthProvider
import com.tencent.devops.scm.sdk.gitlab.auth.GitlabTokenAuthProvider

internal class GitlabPrivateTokenAuthProviderAdapter : GitlabAuthProviderAdapter {
    override fun support(auth: IScmAuth) =
        auth is PersonalAccessTokenScmAuth || auth is TokenSshPrivateKeyScmAuth

    override fun get(auth: IScmAuth): GitlabAuthProvider = when (auth) {
        is PersonalAccessTokenScmAuth -> GitlabTokenAuthProvider.personalAccessToken(auth.personalAccessToken)
        is TokenSshPrivateKeyScmAuth -> GitlabTokenAuthProvider.privateToken(auth.token)
        else -> throw UnsupportedOperationException("unsupported GitLab auth type: ${auth.javaClass.name}")
    }
}
