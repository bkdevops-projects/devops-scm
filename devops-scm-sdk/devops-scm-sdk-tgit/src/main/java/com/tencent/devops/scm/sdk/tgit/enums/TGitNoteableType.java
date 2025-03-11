package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitNoteableType {
    COMMIT("Commit"),
    REVIEW("Review"),
    ISSUE("Issue")
    ;

    public final String value;

    TGitNoteableType(String value) {
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
