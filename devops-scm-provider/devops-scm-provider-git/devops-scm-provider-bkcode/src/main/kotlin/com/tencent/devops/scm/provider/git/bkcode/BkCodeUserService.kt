package com.tencent.devops.scm.provider.git.bkcode

import com.tencent.devops.scm.api.UserService
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiFactory

/**
 * TGit 用户服务实现类
 * @property apiFactory TGit API工厂
 */
class BkCodeUserService(
    private val apiFactory: BkCodeApiFactory
) : UserService {

    /**
     * 查找当前用户
     * @param auth 认证信息
     * @return 用户信息
     */
    override fun find(auth: IScmAuth): User {
        return BkCodeApiTemplate.execute(auth, apiFactory) { tGitApi ->
            val tGitUser = tGitApi.userApi.currentUser
            BkCodeObjectConverter.convertUser(tGitUser)
        }
    }
}
