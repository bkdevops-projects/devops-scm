package com.tencent.devops.scm.provider.git.gitee.auth;

import com.tencent.devops.scm.api.pojo.auth.AccessTokenScmAuth;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth;
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth;
import com.tencent.devops.scm.api.pojo.auth.TokenUserPassScmAuth;
import com.tencent.devops.scm.sdk.gitee.auth.GiteeTokenAuthProvider;

public class GiteeTokenAuthProviderAdapter {

    public static boolean support(IScmAuth auth) {
        return auth instanceof AccessTokenScmAuth
                || auth instanceof PersonalAccessTokenScmAuth
                || auth instanceof TokenUserPassScmAuth
                || auth instanceof TokenSshPrivateKeyScmAuth;
    }

    public static GiteeTokenAuthProvider get(IScmAuth auth) {
        if (auth instanceof AccessTokenScmAuth) {
            return GiteeTokenAuthProvider.fromOauthToken(((AccessTokenScmAuth) auth).getAccessToken());
        }
        if (auth instanceof PersonalAccessTokenScmAuth) {
            return GiteeTokenAuthProvider.fromPersonalAccessToken(
                    ((PersonalAccessTokenScmAuth) auth).getPersonalAccessToken());
        }
        if (auth instanceof TokenUserPassScmAuth) {
            return GiteeTokenAuthProvider.fromPersonalAccessToken(((TokenUserPassScmAuth) auth).getToken());
        }
        if (auth instanceof TokenSshPrivateKeyScmAuth) {
            return GiteeTokenAuthProvider.fromPersonalAccessToken(((TokenSshPrivateKeyScmAuth) auth).getToken());
        }
        throw new UnsupportedOperationException(String.format("gitAuth(%s) is not support", auth));
    }
}
