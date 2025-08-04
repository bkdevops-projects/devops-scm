package com.tencent.devops.scm.sdk.gitee.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectVisibility {
    // 公开
    PUBLIC("public"),
    // 私有
    PRIVATE("private"),
    // 所有
    ALL("all");

    private final String value;

    ProjectVisibility(String value) {
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
