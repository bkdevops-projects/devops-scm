package com.tencent.devops.scm.provider.git.gitlab.auth

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.sdk.gitlab.auth.GitlabAuthProvider

internal interface GitlabAuthProviderAdapter {
    fun support(auth: IScmAuth): Boolean
    fun get(auth: IScmAuth): GitlabAuthProvider
}
