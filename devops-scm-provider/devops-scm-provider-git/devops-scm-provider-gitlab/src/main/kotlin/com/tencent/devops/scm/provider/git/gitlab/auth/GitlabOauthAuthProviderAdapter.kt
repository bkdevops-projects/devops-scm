package com.tencent.devops.scm.provider.git.gitlab.auth

import com.tencent.devops.scm.api.pojo.auth.AccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.sdk.gitlab.auth.GitlabAuthProvider
import com.tencent.devops.scm.sdk.gitlab.auth.GitlabTokenAuthProvider

internal class GitlabOauthAuthProviderAdapter : GitlabAuthProviderAdapter {
    override fun support(auth: IScmAuth) = auth is AccessTokenScmAuth

    override fun get(auth: IScmAuth): GitlabAuthProvider {
        require(auth is AccessTokenScmAuth) { "unsupported GitLab auth type: ${auth.javaClass.name}" }
        return GitlabTokenAuthProvider.oauthAccessToken(auth.accessToken)
    }
}
