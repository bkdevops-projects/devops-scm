package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.GitOauth2ClientProperties;
import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.gitlab.auth.GitlabAuthProvider;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabOauth2AccessToken;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class GitlabOauth2Api {
    private static final GitlabAuthProvider ANONYMOUS = builder -> { };
    private final GitOauth2ClientProperties properties;
    private final GitlabApiClient client;

    public GitlabOauth2Api(GitOauth2ClientProperties properties, ScmConnector connector) {
        if (properties == null) {
            throw new IllegalArgumentException("OAuth properties cannot be null");
        }
        this.properties = properties;
        this.client = new GitlabApiClient(normalizeWebUrl(properties.getWebUrl()), connector, ANONYMOUS);
    }

    public String authorizationUrl(String state) {
        return ScmRequest.newBuilder().withApiUrl(client.getApiUrl()).withUrlPath("oauth/authorize")
                .with("client_id", properties.getClientId()).with("redirect_uri", properties.getRedirectUri())
                .with("response_type", "code").with("state", state).build().url().toString();
    }

    private static String normalizeWebUrl(String webUrl) {
        if (webUrl == null || webUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("OAuth webUrl cannot be blank");
        }
        int end = webUrl.length();
        while (end > 0 && webUrl.charAt(end - 1) == '/') {
            end--;
        }
        return webUrl.substring(0, end);
    }

    public GitlabOauth2AccessToken callback(String code) {
        LinkedHashMap<String, String> form = baseForm("authorization_code");
        form.put("code", code);
        return token(form);
    }

    public GitlabOauth2AccessToken refresh(String refreshToken) {
        LinkedHashMap<String, String> form = baseForm("refresh_token");
        form.put("refresh_token", refreshToken);
        return token(form);
    }

    private LinkedHashMap<String, String> baseForm(String grantType) {
        LinkedHashMap<String, String> form = new LinkedHashMap<>();
        form.put("grant_type", grantType);
        form.put("client_id", properties.getClientId());
        form.put("client_secret", properties.getClientSecret());
        form.put("redirect_uri", properties.getRedirectUri());
        return form;
    }

    private GitlabOauth2AccessToken token(Map<String, String> form) {
        return new Requester(client).method(ScmHttpMethod.POST).withUrlPath("oauth/token")
                .contentType("application/x-www-form-urlencoded").with(formBody(form))
                .fetch(GitlabOauth2AccessToken.class);
    }

    private String formBody(Map<String, String> form) {
        StringBuilder body = new StringBuilder();
        for (Map.Entry<String, String> entry : form.entrySet()) {
            if (entry.getValue() == null) {
                throw new IllegalArgumentException(entry.getKey() + " cannot be null");
            }
            if (body.length() > 0) {
                body.append('&');
            }
            body.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            body.append('=');
            body.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return body.toString();
    }
}
