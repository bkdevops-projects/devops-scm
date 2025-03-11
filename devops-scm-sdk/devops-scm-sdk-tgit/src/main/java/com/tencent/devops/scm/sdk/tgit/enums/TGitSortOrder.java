package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitSortOrder {
    ASC("asc"),
    DESC("desc");

    private final String value;

    @JsonValue
    public String toValue() {
        return value;
    }

    TGitSortOrder(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
