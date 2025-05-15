package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitTapdWorkType {

    MR("mr"),
    CR("cr"),
    ISSUE("issue");

    private final String value;

    @JsonValue
    public String toValue() {
        return value;
    }

    TGitTapdWorkType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
