package com.tencent.devops.scm.provider.git.tgit;

import com.tencent.devops.scm.api.TokenService;
import com.tencent.devops.scm.api.pojo.Oauth2AccessToken;
import com.tencent.devops.scm.sdk.tgit.TGitOauth2Api;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitOauth2AccessToken;

public class TGitTokenService implements TokenService {
    private final TGitOauth2Api oauth2Api;

    public TGitTokenService(TGitOauth2Api oauth2Api) {
        this.oauth2Api = oauth2Api;
    }

    @Override
    public String authorizationUrl(String state) {
        return oauth2Api.authorizationUrl(state);
    }

    @Override
    public Oauth2AccessToken callback(String code) {
        TGitOauth2AccessToken accessToken = oauth2Api.callback(code);
        return convertAccessToken(accessToken);
    }

    @Override
    public Oauth2AccessToken refresh(String refreshToken) {
        TGitOauth2AccessToken accessToken = oauth2Api.refresh(refreshToken);
        return convertAccessToken(accessToken);
    }

    private Oauth2AccessToken convertAccessToken(TGitOauth2AccessToken src) {
        return Oauth2AccessToken.builder()
                .accessToken(src.getAccessToken())
                .tokenType(src.getTokenType())
                .expiresIn(src.getExpiresIn())
                .refreshToken(src.getRefreshToken())
                .scope(src.getScope())
                .build();
    }
}
