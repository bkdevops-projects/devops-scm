package com.tencent.devops.scm.sdk.gitee.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GiteeBranchOrderBy {
    NAME("name"),
    UPDATED("updated");

    private final String value;

    GiteeBranchOrderBy(String value) {
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
