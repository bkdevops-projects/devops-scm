package com.tencent.devops.scm.spring.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * oauth2 属性
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Oauth2ClientProperties {
    /**
     * 服务端web url,用于oauth2时页面跳转
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
