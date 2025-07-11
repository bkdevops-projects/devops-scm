package com.tencent.devops.scm.api.pojo.auth

class SshPrivateKeyScmAuth(
    val privateKey: String,
    val passphrase: String? = null
) : IScmAuth {
    companion object {
        const val CLASS_TYPE = "SSH_PRIVATEKEY"
    }
}
