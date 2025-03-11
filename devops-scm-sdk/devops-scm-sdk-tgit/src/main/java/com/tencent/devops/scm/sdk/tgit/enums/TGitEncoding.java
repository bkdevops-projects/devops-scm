package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitEncoding {
    TEXT("text"),
    BASE64("base64");
    private final String value;

    @JsonValue
    public String toValue() {
        return value;
    }

    TGitEncoding(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
