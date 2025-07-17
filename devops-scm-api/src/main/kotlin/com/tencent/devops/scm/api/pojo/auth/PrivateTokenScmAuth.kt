package com.tencent.devops.scm.api.pojo.auth

class PrivateTokenScmAuth(val privateToken: String) : IScmAuth {
    companion object {
        const val CLASS_TYPE = "PRIVATE_TOKEN"
    }
}
