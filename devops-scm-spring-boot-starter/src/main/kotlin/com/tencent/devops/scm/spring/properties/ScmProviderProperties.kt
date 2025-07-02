package com.tencent.devops.scm.spring.properties

/**
 * 提供者属性
 */
data class ScmProviderProperties(
    // 提供者编码
    var providerCode: String? = null,
    // 提供者类型
    var providerType: String? = null,
    // 是否需要代理
    var proxyEnabled: Boolean? = null,
    // 是否开启oauth2
    var oauth2Enabled: Boolean? = null,
    var httpClientProperties: HttpClientProperties? = null,
    // oauth2客户端属性
    var oauth2ClientProperties: Oauth2ClientProperties? = null,
    // webhook属性
    var webhookProperties: WebhookProperties? = null
)