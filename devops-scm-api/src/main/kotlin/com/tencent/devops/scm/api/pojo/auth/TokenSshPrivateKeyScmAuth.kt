package com.tencent.devops.scm.api.pojo.auth

class TokenSshPrivateKeyScmAuth(
    val token: String,
    val privateKey: String,
    val passphrase: String? = null
) : IScmAuth {
    companion object {
        const val CLASS_TYPE: String = "TOKEN_SSH_PRIVATEKEY"
    }
}
