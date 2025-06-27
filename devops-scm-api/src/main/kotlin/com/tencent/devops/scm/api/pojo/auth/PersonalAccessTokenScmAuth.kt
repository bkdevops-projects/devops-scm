package com.tencent.devops.scm.api.pojo.auth

data class PersonalAccessTokenScmAuth(
    val personalAccessToken: String
) : IScmAuth {
    companion object {
        const val CLASS_TYPE = "PERSONAL_ACCESS_TOKEN"
    }
}
