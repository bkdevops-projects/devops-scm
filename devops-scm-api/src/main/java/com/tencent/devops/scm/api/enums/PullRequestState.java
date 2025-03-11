package com.tencent.devops.scm.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PullRequestState {
    ALL("all"),
    OPENED("opened"),
    REOPENED("reopened"),
    MERGED("merged"),
    CLOSED("closed");

    public final String value;

    PullRequestState(String value) {
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
