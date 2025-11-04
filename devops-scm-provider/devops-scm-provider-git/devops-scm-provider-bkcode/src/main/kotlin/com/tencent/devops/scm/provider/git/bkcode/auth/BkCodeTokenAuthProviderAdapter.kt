package com.tencent.devops.scm.provider.git.bkcode.auth

import com.tencent.devops.scm.api.pojo.auth.AccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.PrivateTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenUserPassScmAuth
import com.tencent.devops.scm.sdk.bkcode.auth.BkCodeTokenAuthProvider

/**
 * BkCode Token授权提供者适配器实现类
 */
object BkCodeTokenAuthProviderAdapter {

    /**
     * 判断是否支持该授权类型
     * @param auth 授权信息
     * @return 是否支持
     */
    fun support(auth: IScmAuth): Boolean {
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
    fun get(auth: IScmAuth): BkCodeTokenAuthProvider {
        return when (auth) {
            is AccessTokenScmAuth -> BkCodeTokenAuthProvider.fromOauthToken(auth.accessToken)
            is PersonalAccessTokenScmAuth -> BkCodeTokenAuthProvider.fromPersonalAccessToken(auth.personalAccessToken)
            is TokenUserPassScmAuth -> BkCodeTokenAuthProvider.fromPersonalAccessToken(auth.token)
            is TokenSshPrivateKeyScmAuth -> BkCodeTokenAuthProvider.fromPersonalAccessToken(auth.token)
            else -> throw UnsupportedOperationException("gitAuth($auth) is not support")
        }
    }
}
