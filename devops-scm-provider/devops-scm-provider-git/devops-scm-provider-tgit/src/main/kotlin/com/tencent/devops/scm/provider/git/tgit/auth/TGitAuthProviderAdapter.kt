package com.tencent.devops.scm.provider.git.tgit.auth

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.sdk.tgit.auth.TGitAuthProvider

/**
 * TGit授权提供者适配器接口
 */
interface TGitAuthProviderAdapter {
    /**
     * 判断是否支持该授权类型
     * @param auth 授权信息
     * @return 是否支持
     */
    fun support(auth: IScmAuth): Boolean

    /**
     * 获取对应的授权提供者
     * @param auth 授权信息
     * @return 授权提供者实例
     */
    fun get(auth: IScmAuth): TGitAuthProvider
}
