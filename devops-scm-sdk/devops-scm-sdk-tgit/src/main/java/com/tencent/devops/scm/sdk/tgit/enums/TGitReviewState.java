package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitReviewState {
    EMPTY("empty"),
    APPROVING("approving"),
    APPROVED("approved"),
    CHANGE_REQUIRED("change_required"),
    CHANGE_DENIED("change_denied"),
    REOPENED("reopened"), // commit review 状态
    CLOSED("closed");

    private final String value;

    TGitReviewState(String value) {
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
