package com.tencent.devops.scm.sdk.gitee.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gitee 检查结论枚举
 */
public enum GiteeCheckRunConclusion {
    NEUTRAL("neutral"),
    SUCCESS("success"),
    FAILURE("failure"),
    CANCELLED("cancelled"),
    ACTION_REQUIRED("action_required"),
    TIMED_OUT("timed_out"),
    SKIPPED("skipped");

    private final String value;

    GiteeCheckRunConclusion(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * 根据字符串值解析枚举（忽略大小写，匹配时建议严格，如需宽松可放开）
     *
     * @param value 要解析的字符串（如 "success"）
     * @return 对应的枚举实例
     * @throws IllegalArgumentException 如果值不匹配
     */
    public static GiteeCheckRunConclusion fromValue(String value) {
        for (GiteeCheckRunConclusion status : GiteeCheckRunConclusion.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid Status value: " + value);
    }
}
