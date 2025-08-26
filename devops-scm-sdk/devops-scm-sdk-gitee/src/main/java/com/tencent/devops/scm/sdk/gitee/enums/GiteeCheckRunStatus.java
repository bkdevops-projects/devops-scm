package com.tencent.devops.scm.sdk.gitee.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gitee 检查状态枚举
 */
public enum GiteeCheckRunStatus {
    IN_PROGRESS("in_progress"),
    QUEUED("queued"),
    COMPLETED("completed");

    private final String value;

    GiteeCheckRunStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
