package com.tencent.devops.scm.provider.git.gitee

import com.tencent.devops.scm.api.UserService
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory

/**
 * Gitee 用户服务实现类
 * @property apiFactory Gitee API工厂
 */
class GiteeUserService(
    private val apiFactory: GiteeApiFactory
) : UserService {

    /**
     * 查找当前用户
     * @param auth 认证信息
     * @return 用户信息
     */
    override fun find(auth: IScmAuth): User {
        return GiteeApiTemplate.execute(auth, apiFactory) { tGitApi ->
            val tGitUser = tGitApi.userApi.currentUser
            GiteeObjectConverter.convertUser(tGitUser)
        }
    }
}
