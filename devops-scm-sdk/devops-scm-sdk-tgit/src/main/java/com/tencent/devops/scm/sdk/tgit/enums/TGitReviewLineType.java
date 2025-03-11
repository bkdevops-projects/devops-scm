package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitReviewLineType {
    OLD("old"),
    NEW("new");

    private final String value;

    TGitReviewLineType(String value) {
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
