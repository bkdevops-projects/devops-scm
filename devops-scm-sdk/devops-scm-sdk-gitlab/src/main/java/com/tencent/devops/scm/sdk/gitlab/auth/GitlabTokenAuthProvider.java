package com.tencent.devops.scm.sdk.gitlab.auth;

import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.gitlab.GitlabConstants;

public final class GitlabTokenAuthProvider implements GitlabAuthProvider {
    private final String header;
    private final String value;

    private GitlabTokenAuthProvider(String header, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("token cannot be blank");
        }
        this.header = header;
        this.value = value;
    }

    public static GitlabAuthProvider oauthAccessToken(String token) {
        return new GitlabTokenAuthProvider(
                GitlabConstants.AUTHORIZATION_HEADER, GitlabConstants.BEARER_PREFIX + requireToken(token));
    }

    public static GitlabAuthProvider personalAccessToken(String token) {
        return privateToken(token);
    }

    public static GitlabAuthProvider privateToken(String token) {
        return new GitlabTokenAuthProvider(GitlabConstants.PRIVATE_TOKEN_HEADER, token);
    }

    private static String requireToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("token cannot be blank");
        }
        return token;
    }

    @Override
    public void authorization(ScmRequest.Builder<?> builder) {
        builder.setHeader(header, value);
    }
}
