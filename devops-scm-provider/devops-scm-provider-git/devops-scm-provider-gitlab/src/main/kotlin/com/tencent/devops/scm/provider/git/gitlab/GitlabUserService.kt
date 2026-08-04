package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.UserService
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.sdk.gitlab.GitlabApiFactory

class GitlabUserService(private val apiFactory: GitlabApiFactory) : UserService {
    override fun find(auth: IScmAuth): User = GitlabApiTemplate.execute(auth, apiFactory) { api ->
        GitlabObjectConverter.convertUser(api.usersApi.currentUser)
    }
}
