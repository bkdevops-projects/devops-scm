package com.tencent.devops.scm.provider.svn.tsvn.auth;


import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth;
import com.tencent.devops.scm.api.pojo.auth.TokenUserPassScmAuth;
import com.tencent.devops.scm.api.pojo.auth.UserPassScmAuth;
import com.tencent.devops.scm.sdk.tsvn.auth.TSvnAuthProvider;
import com.tencent.devops.scm.sdk.tsvn.auth.TSvnTokenAuthProvider;
import com.tencent.devops.scm.sdk.tsvn.auth.TSvnUserPassAuthProvider;

public class TSvnAuthProviderAdapter {
    public static boolean support(IScmAuth auth) {
        return auth instanceof UserPassScmAuth
                || auth instanceof TokenUserPassScmAuth
                || auth instanceof TokenSshPrivateKeyScmAuth;
    }

    public static TSvnAuthProvider get(IScmAuth auth) {
        if (auth instanceof UserPassScmAuth) {
            return new TSvnUserPassAuthProvider(
                    ((UserPassScmAuth) auth).getUsername(),
                    ((UserPassScmAuth) auth).getPassword()
            );
        }
        if (auth instanceof TokenUserPassScmAuth) {
            return TSvnTokenAuthProvider.fromPrivateToken(
                    ((TokenUserPassScmAuth) auth).getToken()
            );
        }
        if (auth instanceof TokenSshPrivateKeyScmAuth) {
            return TSvnTokenAuthProvider.fromPrivateToken(
                    ((TokenSshPrivateKeyScmAuth) auth).getToken()
            );
        }
        throw new UnsupportedOperationException(String.format("gitAuth(%s) is not support", auth));
    }
}
