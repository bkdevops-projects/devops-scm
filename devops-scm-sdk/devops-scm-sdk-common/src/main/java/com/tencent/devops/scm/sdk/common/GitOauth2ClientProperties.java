package com.tencent.devops.scm.sdk.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitOauth2ClientProperties {

    /**
     * 服务端url
     */
    private String webUrl;

    /**
     * 应用ID
     */
    private String clientId;
    /**
     * 应用密钥
     */
    private String clientSecret;
    /**
     * 授权回调url
     */
    private String redirectUri;
}
