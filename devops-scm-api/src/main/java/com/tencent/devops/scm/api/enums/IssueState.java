package com.tencent.devops.scm.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IssueState {
    ALL("all"),
    OPENED("opened"),
    REOPENED("reopened"),
    CLOSED("closed");

    public final String value;

    IssueState(String value) {
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
