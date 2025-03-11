package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitMergeRequestState {
    OPENED("opened"),
    MERGED("merged"),
    REOPENED("reopened"),
    CLOSED("closed");

    public final String value;

    TGitMergeRequestState(String value) {
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
