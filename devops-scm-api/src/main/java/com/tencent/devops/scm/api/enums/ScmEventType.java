package com.tencent.devops.scm.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * scm通用事件类型
 */
public enum ScmEventType {
    ISSUE("issue"),
    ISSUE_COMMENT("issue_comment"),
    PULL_REQUEST("pull_request"),
    PULL_REQUEST_COMMENT("pull_request_comment"),
    PUSH("push"),
    TAG("tag"),
    PULL_REQUEST_REVIEW("pull_request_review");

    public final String value;

    ScmEventType(String value) {
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

    public static ScmEventType fromValue(String value) {
        for (ScmEventType type : ScmEventType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}
