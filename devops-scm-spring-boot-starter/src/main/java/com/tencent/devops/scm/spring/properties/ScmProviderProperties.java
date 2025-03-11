package com.tencent.devops.scm.spring.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScmProviderProperties {
    // 提供者编码
    private String providerCode;
    // 提供者类型
    private String providerType;
    // 是否需要代理
    private Boolean proxyEnabled;
    // 是否开启oauth2
    private Boolean oauth2Enabled;

    private HttpClientProperties httpClientProperties;
    // oauth2客户端属性
    private Oauth2ClientProperties oauth2ClientProperties;
    // webhook属性
    private WebhookProperties webhookProperties;
}
