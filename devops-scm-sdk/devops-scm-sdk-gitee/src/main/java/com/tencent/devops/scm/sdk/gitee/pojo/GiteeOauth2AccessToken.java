package com.tencent.devops.scm.sdk.gitee.pojo;

import lombok.Data;

@Data
public class GiteeOauth2AccessToken {
    private String accessToken;
    private String tokenType;
    // 单位: 秒
    private Long expiresIn;
    private String refreshToken;
    private String scope;
    private Long createdAt;
}
