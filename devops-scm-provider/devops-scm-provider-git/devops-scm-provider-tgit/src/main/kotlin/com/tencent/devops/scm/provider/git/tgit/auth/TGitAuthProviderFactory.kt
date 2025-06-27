package com.tencent.devops.scm.provider.git.tgit.auth

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.sdk.tgit.auth.TGitAuthProvider

/**
 * TGit授权提供者工厂类
 */
object TGitAuthProviderFactory {
    // 初始化适配器列表
    private val adapters = listOf(
        TGitUserPassAuthProviderAdapter(),
        TGitTokenAuthProviderAdapter()
    )

    /**
     * 创建对应的授权提供者
     * @param gitAuth 授权信息
     * @return 授权提供者实例
     * @throws UnsupportedOperationException 当授权类型不支持时抛出
     */
    fun create(gitAuth: IScmAuth): TGitAuthProvider {
        return adapters.firstOrNull { it.support(gitAuth) }?.get(gitAuth)
            ?: throw UnsupportedOperationException("gitAuth($gitAuth) is not support")
    }
}
