package com.tencent.devops.scm.provider.git.gitee.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GiteeEventType {
    PUSH("push"),
    TAG_PUSH("tag_push"),
    MERGE_REQUEST("merge_request"),
    ISSUES("issues"),
    NOTE("note"),
    REVIEW("review");

    private final String value;

    @JsonValue
    public String toValue() {
        return value;
    }

    GiteeEventType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
