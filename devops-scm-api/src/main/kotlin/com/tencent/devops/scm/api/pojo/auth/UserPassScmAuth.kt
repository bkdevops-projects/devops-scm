package com.tencent.devops.scm.api.pojo.auth

class UserPassScmAuth(
    val username: String,
    val password: String
) : IScmAuth {
    companion object {
        const val CLASS_TYPE: String = "USER_PASS"
    }
}
