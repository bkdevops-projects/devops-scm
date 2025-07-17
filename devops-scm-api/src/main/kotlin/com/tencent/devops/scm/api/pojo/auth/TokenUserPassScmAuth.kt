package com.tencent.devops.scm.api.pojo.auth

class TokenUserPassScmAuth(
    val token: String,
    val username: String,
    val password: String
) : IScmAuth {
    companion object {
        const val CLASS_TYPE = "TOKEN_USER_PASS"
    }
}
