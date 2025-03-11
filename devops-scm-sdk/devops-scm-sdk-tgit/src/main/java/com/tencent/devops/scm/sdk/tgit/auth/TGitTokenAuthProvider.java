package com.tencent.devops.scm.sdk.tgit.auth;

import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.tgit.TGitConstants;

import static com.tencent.devops.scm.sdk.tgit.TGitConstants.OAUTH_TOKEN_HEADER;
import static com.tencent.devops.scm.sdk.tgit.TGitConstants.PRIVATE_TOKEN_HEADER;

/**
 * token授权,token包含oauth2_token、private_token、personal_access_token
 */
public class TGitTokenAuthProvider implements TGitAuthProvider {

    private final String authHeader;
    private final String authToken;

    public TGitTokenAuthProvider(String authHeader, String authToken) {
        this.authHeader = authHeader;
        this.authToken = authToken;
    }

    public static TGitAuthProvider fromOauthToken(String oauthAccessToken) {
        return new TGitTokenAuthProvider(OAUTH_TOKEN_HEADER, oauthAccessToken);
    }

    public static TGitAuthProvider fromPrivateToken(String privateToken) {
        return new TGitTokenAuthProvider(PRIVATE_TOKEN_HEADER, privateToken);
    }

    public static TGitAuthProvider fromPersonalAccessToken(String personalAccessToken) {
        return new TGitTokenAuthProvider(PRIVATE_TOKEN_HEADER, personalAccessToken);
    }

    public static TGitAuthProvider fromTokenType(TGitConstants.TokenType tokenType, String authToken) {
        switch (tokenType) {
            case PRIVATE:
                return fromPrivateToken(authToken);
            case PERSONAL_ACCESS:
                return fromPersonalAccessToken(authToken);
            case OAUTH2_ACCESS:
                return fromOauthToken(authToken);
            default:
                throw new UnsupportedOperationException(String.format("tokenType(%s) is not support", tokenType));
        }
    }

    @Override
    public void authorization(ScmRequest.Builder<?> builder) {
        builder.setHeader(authHeader, authToken);
    }
}
