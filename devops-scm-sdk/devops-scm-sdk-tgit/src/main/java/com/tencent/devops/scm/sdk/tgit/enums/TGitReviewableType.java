package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitReviewableType {
    COMPARISON("comparison"),
    MERGE_REQUEST("merge_request");

    private final String value;

    TGitReviewableType(String value) {
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
