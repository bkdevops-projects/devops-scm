package com.tencent.devops.scm.sdk.bkcode;

import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeOauth2AccessToken;
import com.tencent.devops.scm.sdk.common.GitOauth2ClientProperties;
import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.common.util.UrlEncoder;

public class BkCodeOauth2Api {

    private final GitOauth2ClientProperties properties;
    private final BkCodeApiClient client;
    private static final String OAUTH2_URI_PATTERN = "oauth/authorize";
    private static final String OAUTH2_CALLBACK_URI_PATTERN =
            "oauth/token?client_id=:client_id&"
                    + "client_secret=:client_secret&"
                    + "code=:code&"
                    + "grant_type=authorization_code&"
                    + "redirect_uri=:redirect_uri";
    private static final String OAUTH2_REFRESH_TOKEN_URI_PATTERN =
            "oauth/token?client_id=:client_id&"
                    + "client_secret=:client_secret&"
                    + "refresh_token=:refresh_token&"
                    + "grant_type=refresh_token&"
                    + "redirect_uri=:redirect_uri";

    public BkCodeOauth2Api(GitOauth2ClientProperties properties, ScmConnector connector) {
        this.properties = properties;
        this.client = new BkCodeApiClient(properties.getWebUrl(), connector, HttpAuthProvider.ANONYMOUS);
    }

    // client_id=APP_ID&redirect_uri=REDIRECT_URI&response_type=code&state=YOUR_UNIQUE_STATE_HASH
    public String authorizationUrl(String state) {
        return ScmRequest.newBuilder()
                .withApiUrl(properties.getWebUrl())
                .withUrlPath(OAUTH2_URI_PATTERN)
                .with("client_id", properties.getClientId())
                .with("redirect_uri", properties.getRedirectUri())
                .with("response_type", "code")
                .with("state", state)
                .build()
                .url()
                .toString();
    }

    public BkCodeOauth2AccessToken callback(String code) {
        return new Requester(client)
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        OAUTH2_CALLBACK_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("client_id", properties.getClientId())
                                .add("client_secret", properties.getClientSecret())
                                .add("code", code)
                                .add("redirect_uri", UrlEncoder.urlEncode(properties.getRedirectUri()))
                                .build()
                )
                .contentType("application/x-www-form-urlencoded")
                .with("")
                .fetch(BkCodeOauth2AccessToken.class);
    }

    public BkCodeOauth2AccessToken refresh(String refreshToken) {
        return new Requester(client)
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        OAUTH2_REFRESH_TOKEN_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("client_id", properties.getClientId())
                                .add("client_secret", properties.getClientSecret())
                                .add("refresh_token", refreshToken)
                                .add("redirect_uri", UrlEncoder.urlEncode(properties.getRedirectUri()))
                                .build()
                )
                .contentType("application/x-www-form-urlencoded")
                .with("")
                .fetch(BkCodeOauth2AccessToken.class);
    }
}
