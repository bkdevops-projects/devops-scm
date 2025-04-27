package com.tencent.devops.scm.provider.gitee.simple.auth;

import com.gitee.sdk.gitee5j.ApiClient;
import com.gitee.sdk.gitee5j.auth.OAuth;
import com.tencent.devops.scm.api.pojo.auth.AccessTokenScmAuth;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth;
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth;
import com.tencent.devops.scm.api.pojo.auth.TokenUserPassScmAuth;
import lombok.NonNull;

public class GiteeTokenAuthProviderAdapter {

    private static final String AUTH_TYPE_OAUTH = "OAuth2";

    @NonNull
    private ApiClient apiClient;

    public GiteeTokenAuthProviderAdapter(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void withAuth(IScmAuth auth) {
        OAuth oAuth = (OAuth) apiClient.getAuthentication(AUTH_TYPE_OAUTH);
        String token = "";
        if (auth instanceof AccessTokenScmAuth) {
            token = ((AccessTokenScmAuth) auth).getAccessToken();
        } else if (auth instanceof PersonalAccessTokenScmAuth) {
            token = ((PersonalAccessTokenScmAuth) auth).getPersonalAccessToken();
        } else if (auth instanceof TokenUserPassScmAuth) {
            token = ((TokenUserPassScmAuth) auth).getToken();
        } else if (auth instanceof TokenSshPrivateKeyScmAuth) {
            token = ((TokenSshPrivateKeyScmAuth) auth).getToken();
        } else {
            throw new UnsupportedOperationException(String.format("gitAuth(%s) is not support", auth));
        }
        oAuth.setAccessToken(token);
    }
}