package com.tencent.devops.scm.provider.svn.tsvn.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TSvnEventType {
    POST_COMMIT("post_commit"),
    PRE_COMMIT("pre_commit"),
    POST_LOCK("post_lock"),
    PRE_LOCK("pre_lock");

    private final String value;

    @JsonValue
    public String toValue() {
        return value;
    }

    TSvnEventType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
