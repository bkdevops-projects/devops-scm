package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitMergeRequestOrderBy {

    CREATED_AT("created_at"),
    UPDATED_AT("updated_at"),
    RESOLVE_AT("resolve_at");
    private final String value;

    TGitMergeRequestOrderBy(String value) {
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
