package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitCheckRunState {
    PENDING("pending"),
    SUCCESS("success"),
    ERROR("error"),
    FAILURE("failure");

    private final String value;

    TGitCheckRunState(String value) {
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