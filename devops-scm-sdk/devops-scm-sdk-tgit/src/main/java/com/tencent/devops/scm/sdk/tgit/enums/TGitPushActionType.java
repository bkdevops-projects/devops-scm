package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitPushActionType {
    NEW_BRANCH("new-branch"),
    NEW_BRANCH_AND_PUSH_FILE("new-branch-and-push-file"),
    PUSH_FILE("push-file");

    public final String value;

    TGitPushActionType(String value) {
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
