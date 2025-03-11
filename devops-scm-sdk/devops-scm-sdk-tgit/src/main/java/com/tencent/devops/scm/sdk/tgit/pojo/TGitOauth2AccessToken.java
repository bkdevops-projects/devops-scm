package com.tencent.devops.scm.sdk.tgit.pojo;

import lombok.Data;

@Data
public class TGitOauth2AccessToken {
    private String accessToken;
    private String tokenType;
    // 单位: 秒
    private Long expiresIn;
    private String refreshToken;
    private String scope;
}
