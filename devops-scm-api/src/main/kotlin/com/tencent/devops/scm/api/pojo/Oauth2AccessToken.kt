package com.tencent.devops.scm.api.pojo

data class Oauth2AccessToken(
    // 访问令牌
    val accessToken: String,
    // 令牌类型
    val tokenType: String,
    // 过期时间(单位:秒)
    val expiresIn: Long,
    // 刷新令牌
    val refreshToken: String,
    // 权限范围
    val scope: String? = null
)
