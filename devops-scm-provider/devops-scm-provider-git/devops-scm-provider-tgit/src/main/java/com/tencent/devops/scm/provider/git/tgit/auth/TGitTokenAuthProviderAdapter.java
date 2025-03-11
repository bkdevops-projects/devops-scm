package com.tencent.devops.scm.provider.git.tgit.auth;

import com.tencent.devops.scm.api.pojo.auth.AccessTokenScmAuth;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth;
import com.tencent.devops.scm.api.pojo.auth.PrivateTokenScmAuth;
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth;
import com.tencent.devops.scm.api.pojo.auth.TokenUserPassScmAuth;
import com.tencent.devops.scm.sdk.tgit.auth.TGitAuthProvider;
import com.tencent.devops.scm.sdk.tgit.auth.TGitTokenAuthProvider;

public class TGitTokenAuthProviderAdapter implements TGitAuthProviderAdapter {

    @Override
    public boolean support(IScmAuth auth) {
        return auth instanceof AccessTokenScmAuth
                || auth instanceof PrivateTokenScmAuth
                || auth instanceof PersonalAccessTokenScmAuth
                || auth instanceof TokenUserPassScmAuth
                || auth instanceof TokenSshPrivateKeyScmAuth;
    }

    @Override
    public TGitAuthProvider get(IScmAuth auth) {
        if (auth instanceof AccessTokenScmAuth) {
            return TGitTokenAuthProvider.fromOauthToken(((AccessTokenScmAuth) auth).getAccessToken());
        }
        if (auth instanceof PrivateTokenScmAuth) {
            return TGitTokenAuthProvider.fromPrivateToken(((PrivateTokenScmAuth) auth).getPrivateToken());
        }
        if (auth instanceof PersonalAccessTokenScmAuth) {
            return TGitTokenAuthProvider.fromPersonalAccessToken(
                    ((PersonalAccessTokenScmAuth) auth).getPersonalAccessToken());
        }
        if (auth instanceof TokenUserPassScmAuth) {
            return TGitTokenAuthProvider.fromPrivateToken(((TokenUserPassScmAuth) auth).getToken());
        }
        if (auth instanceof TokenSshPrivateKeyScmAuth) {
            return TGitTokenAuthProvider.fromPrivateToken(((TokenSshPrivateKeyScmAuth) auth).getToken());
        }
        throw new UnsupportedOperationException(String.format("gitAuth(%s) is not support", auth));
    }
}
