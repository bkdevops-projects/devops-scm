package com.tencent.devops.scm.sdk.gitee;

import com.tencent.devops.scm.sdk.common.GitOauth2ClientProperties;
import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.common.UriTemplate;
import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.common.util.UrlEncoder;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeOauth2AccessToken;

public class GiteeOauth2Api {

    private final GitOauth2ClientProperties properties;
    private final GiteeApiClient client;
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

    public GiteeOauth2Api(GitOauth2ClientProperties properties, ScmConnector connector) {
        this.properties = properties;
        this.client = new GiteeApiClient(properties.getWebUrl(), connector, HttpAuthProvider.ANONYMOUS);
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

    public GiteeOauth2AccessToken callback(String code) {
        String urlPath = UriTemplate.from(OAUTH2_CALLBACK_URI_PATTERN)
                .with("client_id", properties.getClientId())
                .with("client_secret", properties.getClientSecret())
                .with("code", code)
                .with("redirect_uri", properties.getRedirectUri(), true)
                .expand();
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
                .fetch(GiteeOauth2AccessToken.class);
    }

    public GiteeOauth2AccessToken refresh(String refreshToken) {
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
                .fetch(GiteeOauth2AccessToken.class);
    }
}
