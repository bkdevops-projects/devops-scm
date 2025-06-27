package com.tencent.devops.scm.provider.git.tgit.auth

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.auth.UserPassScmAuth
import com.tencent.devops.scm.sdk.tgit.auth.TGitAuthProvider
import com.tencent.devops.scm.sdk.tgit.auth.TGitUserPassAuthProvider

/**
 * TGit 用户名密码授权提供者适配器
 */
class TGitUserPassAuthProviderAdapter : TGitAuthProviderAdapter {

    /**
     * 检查是否支持该授权类型
     * @param auth 授权信息
     * @return 当授权类型为UserPassScmAuth时返回true
     */
    override fun support(auth: IScmAuth): Boolean {
        return auth is UserPassScmAuth
    }

    /**
     * 获取用户名密码授权提供者
     * @param auth 授权信息
     * @return 用户名密码授权提供者实例
     */
    override fun get(auth: IScmAuth): TGitAuthProvider {
        val userPassGitAuth = auth as UserPassScmAuth
        return TGitUserPassAuthProvider(userPassGitAuth.username, userPassGitAuth.password)
    }
}
