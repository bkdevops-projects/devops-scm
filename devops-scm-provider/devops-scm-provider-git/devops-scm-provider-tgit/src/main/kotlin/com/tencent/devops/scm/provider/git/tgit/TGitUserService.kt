package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.UserService
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.provider.git.tgit.auth.TGitAuthProviderFactory
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory

/**
 * TGit 用户服务实现类
 * @property apiFactory TGit API工厂
 */
class TGitUserService(
    private val apiFactory: TGitApiFactory
) : UserService {

    /**
     * 查找当前用户
     * @param auth 认证信息
     * @return 用户信息
     */
    override fun find(auth: IScmAuth): User {
        return TGitApiTemplate.execute(auth, apiFactory) { tGitApi ->
            val tGitUser = tGitApi.userApi.currentUser
            TGitObjectConverter.convertUser(tGitUser)
        }
    }
}
