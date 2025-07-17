package com.tencent.devops.scm.provider.git.gitee.auth

import com.tencent.devops.scm.api.pojo.auth.AccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenUserPassScmAuth
import com.tencent.devops.scm.sdk.gitee.GiteeConstants.TokenType
import com.tencent.devops.scm.sdk.gitee.auth.GiteeTokenAuthProvider

object GiteeTokenAuthProviderAdapter {

    fun support(auth: IScmAuth): Boolean {
        return auth is AccessTokenScmAuth ||
                auth is PersonalAccessTokenScmAuth ||
                auth is TokenUserPassScmAuth ||
                auth is TokenSshPrivateKeyScmAuth
    }

    fun get(auth: IScmAuth): GiteeTokenAuthProvider {
        return when (auth) {
            is AccessTokenScmAuth -> GiteeTokenAuthProvider.fromTokenType(
                TokenType.OAUTH2_ACCESS,
                auth.accessToken
            )
            
            is PersonalAccessTokenScmAuth -> GiteeTokenAuthProvider.fromTokenType(
                TokenType.PERSONAL_ACCESS,
                auth.personalAccessToken
            )
            
            is TokenUserPassScmAuth -> GiteeTokenAuthProvider.fromTokenType(
                TokenType.PERSONAL_ACCESS,
                auth.token
            )
            
            is TokenSshPrivateKeyScmAuth -> GiteeTokenAuthProvider.fromTokenType(
                TokenType.PERSONAL_ACCESS,
                auth.token
            )
            
            else -> throw UnsupportedOperationException("gitAuth($auth) is not support")
        }
    }
}
