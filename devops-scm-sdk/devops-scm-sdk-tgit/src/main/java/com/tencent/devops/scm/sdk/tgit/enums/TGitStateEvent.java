package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * mr或者issue的更新状态
 */
public enum TGitStateEvent {

    CLOSE("close"),
    REOPEN("reopen");

    private final String value;

    TGitStateEvent(String value) {
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
