package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitIssueOrderBy {

    CREATED_AT("created_at"),
    UPDATED_AT("updated_at");

    private final String value;

    TGitIssueOrderBy(String value) {
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
