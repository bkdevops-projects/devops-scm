package com.tencent.devops.scm.provider.git.tgit.auth

import com.tencent.devops.scm.api.pojo.auth.AccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.PrivateTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenUserPassScmAuth
import com.tencent.devops.scm.sdk.tgit.auth.TGitAuthProvider
import com.tencent.devops.scm.sdk.tgit.auth.TGitTokenAuthProvider

/**
 * TGit Token授权提供者适配器实现类
 */
class TGitTokenAuthProviderAdapter : TGitAuthProviderAdapter {

    /**
     * 判断是否支持该授权类型
     * @param auth 授权信息
     * @return 是否支持
     */
    override fun support(auth: IScmAuth): Boolean {
        return auth is AccessTokenScmAuth ||
                auth is PrivateTokenScmAuth ||
                auth is PersonalAccessTokenScmAuth ||
                auth is TokenUserPassScmAuth ||
                auth is TokenSshPrivateKeyScmAuth
    }

    /**
     * 根据授权信息获取对应的Token授权提供者
     * @param auth 授权信息
     * @return Token授权提供者实例
     * @throws UnsupportedOperationException 当授权类型不支持时抛出
     */
    override fun get(auth: IScmAuth): TGitAuthProvider {
        return when (auth) {
            is AccessTokenScmAuth -> TGitTokenAuthProvider.fromOauthToken(auth.accessToken)
            is PrivateTokenScmAuth -> TGitTokenAuthProvider.fromPrivateToken(auth.privateToken)
            is PersonalAccessTokenScmAuth -> TGitTokenAuthProvider.fromPersonalAccessToken(auth.personalAccessToken)
            is TokenUserPassScmAuth -> TGitTokenAuthProvider.fromPrivateToken(auth.token)
            is TokenSshPrivateKeyScmAuth -> TGitTokenAuthProvider.fromPrivateToken(auth.token)
            else -> throw UnsupportedOperationException("gitAuth($auth) is not support")
        }
    }
}
