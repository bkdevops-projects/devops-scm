package com.tencent.devops.scm.provider.git.gitee

import com.tencent.devops.scm.api.TokenService
import com.tencent.devops.scm.api.pojo.Oauth2AccessToken
import com.tencent.devops.scm.sdk.gitee.GiteeOauth2Api
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeOauth2AccessToken

/**
 * Gitee 令牌服务实现类
 * @property oauth2Api TGit OAuth2 API
 */
class GiteeTokenService(
    private val oauth2Api: GiteeOauth2Api
) : TokenService {

    /**
     * 获取授权URL
     * @param state 状态参数
     * @return 授权URL
     */
    override fun authorizationUrl(state: String): String {
        return oauth2Api.authorizationUrl(state)
    }

    /**
     * 回调处理
     * @param code 授权码
     * @return 访问令牌
     */
    override fun callback(code: String): Oauth2AccessToken {
        val accessToken = oauth2Api.callback(code)
        return convertAccessToken(accessToken)
    }

    /**
     * 刷新令牌
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    override fun refresh(refreshToken: String): Oauth2AccessToken {
        val accessToken = oauth2Api.refresh(refreshToken)
        return convertAccessToken(accessToken)
    }

    private fun convertAccessToken(src: GiteeOauth2AccessToken): Oauth2AccessToken {
        return with(src) {
            Oauth2AccessToken(
                accessToken = accessToken,
                tokenType = tokenType,
                expiresIn = expiresIn,
                refreshToken = refreshToken,
                scope = scope
            )
        }
    }
}
