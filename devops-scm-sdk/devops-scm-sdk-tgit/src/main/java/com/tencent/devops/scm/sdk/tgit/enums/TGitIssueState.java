package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitIssueState {
    OPENED("opened"),
    REOPENED("reopened"),
    CLOSED("closed");

    public final String value;

    TGitIssueState(String value) {
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
