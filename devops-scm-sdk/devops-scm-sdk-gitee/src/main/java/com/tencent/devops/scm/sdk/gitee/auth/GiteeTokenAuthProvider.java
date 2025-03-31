package com.tencent.devops.scm.sdk.gitee.auth;

import static com.tencent.devops.scm.sdk.gitee.GiteeConstants.OAUTH_TOKEN_HEADER;

import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.gitee.GiteeConstants;

/**
 * token授权,token包含oauth2_token、personal_access_token
 */
public class GiteeTokenAuthProvider implements GiteeAuthProvider {

    private final String authHeader;
    private final String authToken;

    public GiteeTokenAuthProvider(String authHeader, String authToken) {
        this.authHeader = authHeader;
        this.authToken = authToken;
    }

    public static GiteeAuthProvider fromOauthToken(String oauthAccessToken) {
        return new GiteeTokenAuthProvider(OAUTH_TOKEN_HEADER, oauthAccessToken);
    }

    public static GiteeAuthProvider fromPersonalAccessToken(String privateToken) {
        return new GiteeTokenAuthProvider(OAUTH_TOKEN_HEADER, privateToken);
    }

    public static GiteeAuthProvider fromTokenType(GiteeConstants.TokenType tokenType, String authToken) {
        String authHeaderValue = "token " + authToken;
        switch (tokenType) {
            case PERSONAL_ACCESS:
                return fromPersonalAccessToken(authHeaderValue);
            case OAUTH2_ACCESS:
                return fromOauthToken(authHeaderValue);
            default:
                throw new UnsupportedOperationException(String.format("tokenType(%s) is not support", tokenType));
        }
    }

    @Override
    public void authorization(ScmRequest.Builder<?> builder) {
        builder.setHeader(authHeader, authToken);
    }
}
