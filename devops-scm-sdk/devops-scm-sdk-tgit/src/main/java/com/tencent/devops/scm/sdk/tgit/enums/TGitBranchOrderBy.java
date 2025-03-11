package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitBranchOrderBy {
    NAME("name"),
    UPDATED("updated");

    private final String value;

    TGitBranchOrderBy(String value) {
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
