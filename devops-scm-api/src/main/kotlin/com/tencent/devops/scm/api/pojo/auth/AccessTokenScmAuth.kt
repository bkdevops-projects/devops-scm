package com.tencent.devops.scm.api.pojo.auth

/**
 * accessToken 鉴权
 */
data class AccessTokenScmAuth(
    val accessToken: String
) : IScmAuth {
    companion object {
        const val CLASS_TYPE = "OAUTH_TOKEN"
    }
}
