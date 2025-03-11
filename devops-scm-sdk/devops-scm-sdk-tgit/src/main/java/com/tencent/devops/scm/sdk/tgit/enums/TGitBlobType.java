package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitBlobType {

    TREE("tree"),
    BLOB("blob"),
    COMMIT("commit");

    private final String value;

    @JsonValue
    public String toValue() {
        return value;
    }

    TGitBlobType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
