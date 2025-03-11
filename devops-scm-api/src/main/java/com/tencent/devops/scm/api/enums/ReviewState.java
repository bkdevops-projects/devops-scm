package com.tencent.devops.scm.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 评审状态
 */
public enum ReviewState {
    UNKNOWN("unknown"),
    APPROVING("approving"),
    APPROVED("approved"),
    CHANGES_REQUESTED("changes_requested"),
    CHANGE_DENIED("change_denied"),
    EMPTY("empty"),
    CLOSED("closed");

    public final String value;

    ReviewState(String value) {
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
