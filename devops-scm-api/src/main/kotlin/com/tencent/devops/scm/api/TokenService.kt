package com.tencent.devops.scm.api

import com.tencent.devops.scm.api.pojo.Oauth2AccessToken

/**
 * 令牌服务接口
 */
interface TokenService {
    /**
     * 获取授权URL
     * @param state 状态参数
     * @return 授权URL
     */
    fun authorizationUrl(state: String): String

    /**
     * 回调处理
     * @param code 授权码
     * @return 访问令牌
     */
    fun callback(code: String): Oauth2AccessToken

    /**
     * 刷新令牌
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    fun refresh(refreshToken: String): Oauth2AccessToken
}
