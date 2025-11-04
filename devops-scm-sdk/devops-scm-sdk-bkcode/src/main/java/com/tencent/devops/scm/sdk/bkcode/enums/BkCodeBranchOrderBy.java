package com.tencent.devops.scm.sdk.bkcode.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BkCodeBranchOrderBy {
    NAME("name"),
    UPDATED("updated");

    private final String value;

    BkCodeBranchOrderBy(String value) {
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
