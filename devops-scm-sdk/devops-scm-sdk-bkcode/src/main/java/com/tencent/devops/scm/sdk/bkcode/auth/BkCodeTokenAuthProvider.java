package com.tencent.devops.scm.sdk.bkcode.auth;

import com.tencent.devops.scm.sdk.bkcode.BkCodeConstants;
import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;

/**
 * token授权,token包含oauth2_token、personal_access_token
 */
public class BkCodeTokenAuthProvider implements HttpAuthProvider {

    private final String authHeader;
    private final String authToken;

    public BkCodeTokenAuthProvider(String authHeader, String authToken) {
        this.authHeader = authHeader;
        this.authToken = authToken;
    }

    public static BkCodeTokenAuthProvider fromOauthToken(String oauthAccessToken) {
        return new BkCodeTokenAuthProvider(BkCodeConstants.OAUTH_TOKEN_HEADER, oauthAccessToken);
    }

    public static BkCodeTokenAuthProvider fromPersonalAccessToken(String privateToken) {
        return new BkCodeTokenAuthProvider(BkCodeConstants.PERSONAL_ACCESS_TOKEN_HEADER, privateToken);
    }

    public static BkCodeTokenAuthProvider fromTokenType(BkCodeConstants.TokenType tokenType, String authToken) {
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
