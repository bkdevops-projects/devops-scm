package com.tencent.devops.scm.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * webhook 认证类型
 * 确认webhook的来源可靠性
 */
public enum WebhookSecretType {
    // 在app中配置secret值,如github app
    APP("app"),
    // 在请求头中传递鉴权的值
    REQUEST_HEADER("request_header");

    public final String value;

    WebhookSecretType(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
