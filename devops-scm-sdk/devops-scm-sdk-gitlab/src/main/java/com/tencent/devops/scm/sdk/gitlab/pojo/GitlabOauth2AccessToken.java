package com.tencent.devops.scm.sdk.gitlab.pojo;

import lombok.Data;

@Data
public class GitlabOauth2AccessToken {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String refreshToken;
    private String scope;
    private Long createdAt;
}
