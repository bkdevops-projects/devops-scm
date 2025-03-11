package com.tencent.devops.scm.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Oauth2AccessToken {

    private String accessToken;
    private String tokenType;
    // 单位: 秒
    private Long expiresIn;
    private String refreshToken;
    private String scope;
}
