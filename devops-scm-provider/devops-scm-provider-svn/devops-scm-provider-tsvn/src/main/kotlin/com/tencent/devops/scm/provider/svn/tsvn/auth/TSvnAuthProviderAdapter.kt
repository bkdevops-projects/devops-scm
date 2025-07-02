package com.tencent.devops.scm.provider.svn.tsvn.auth

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenUserPassScmAuth
import com.tencent.devops.scm.api.pojo.auth.UserPassScmAuth
import com.tencent.devops.scm.sdk.tsvn.auth.TSvnAuthProvider
import com.tencent.devops.scm.sdk.tsvn.auth.TSvnTokenAuthProvider
import com.tencent.devops.scm.sdk.tsvn.auth.TSvnUserPassAuthProvider

object TSvnAuthProviderAdapter {

    fun support(auth: IScmAuth): Boolean {
        return auth is UserPassScmAuth ||
                auth is TokenUserPassScmAuth ||
                auth is TokenSshPrivateKeyScmAuth
    }

    fun get(auth: IScmAuth): TSvnAuthProvider {
        return when (auth) {
            is UserPassScmAuth -> TSvnUserPassAuthProvider(auth.username, auth.password)
            is TokenUserPassScmAuth -> TSvnTokenAuthProvider.fromPrivateToken(auth.token)
            is TokenSshPrivateKeyScmAuth -> TSvnTokenAuthProvider.fromPrivateToken(auth.token)
            else -> throw UnsupportedOperationException("gitAuth($auth) is not support")
        }
    }
}