package com.tencent.devops.scm.sdk.bkcode.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BkCodeRepoOrderField {
    DISPLAY_NAME("displayName"),
    CREATE_TIME("createTime"),
    UPDATE_TIME("updateTime"),
    CREATOR("creator");

    private final String value;

    BkCodeRepoOrderField(String value) {
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
