package com.tencent.devops.scm.api.enums

/**
 * webhook 认证类型
 * 确认webhook的来源可靠性
 */
enum class WebhookSecretType(val value: String) {
    // 在app中配置secret值,如github app
    APP("app"),

    // 在请求头中传递鉴权的值
    REQUEST_HEADER("request_header");
}
