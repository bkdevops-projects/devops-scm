package com.tencent.devops.scm.spring.properties

/**
 * oauth2 属性
 */
data class Oauth2ClientProperties(
    /**
     * 服务端web url,用于oauth2时页面跳转
     */
    var webUrl: String? = null,
    
    /**
     * 应用ID
     */
    var clientId: String? = null,
    
    /**
     * 应用密钥
     */
    var clientSecret: String? = null,
    
    /**
     * 授权回调url
     */
    var redirectUri: String? = null
)