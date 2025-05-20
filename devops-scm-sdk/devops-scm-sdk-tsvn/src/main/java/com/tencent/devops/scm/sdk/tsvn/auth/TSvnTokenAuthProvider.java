package com.tencent.devops.scm.sdk.tsvn.auth;

import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.tsvn.TSvnConstants;

import static com.tencent.devops.scm.sdk.tsvn.TSvnConstants.OAUTH_TOKEN_HEADER;
import static com.tencent.devops.scm.sdk.tsvn.TSvnConstants.PRIVATE_TOKEN_HEADER;

/**
 * token授权,token包含oauth2_token、private_token、personal_access_token
 */
public class TSvnTokenAuthProvider implements TSvnAuthProvider {

    private final String authHeader;
    private final String authToken;

    public TSvnTokenAuthProvider(String authHeader, String authToken) {
        this.authHeader = authHeader;
        this.authToken = authToken;
    }

    public static TSvnAuthProvider fromOauthToken(String oauthAccessToken) {
        return new TSvnTokenAuthProvider(OAUTH_TOKEN_HEADER, oauthAccessToken);
    }

    public static TSvnAuthProvider fromPrivateToken(String privateToken) {
        return new TSvnTokenAuthProvider(PRIVATE_TOKEN_HEADER, privateToken);
    }

    public static TSvnAuthProvider fromPersonalAccessToken(String personalAccessToken) {
        return new TSvnTokenAuthProvider(PRIVATE_TOKEN_HEADER, personalAccessToken);
    }

    public static TSvnAuthProvider fromTokenType(TSvnConstants.TokenType tokenType, String authToken) {
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
