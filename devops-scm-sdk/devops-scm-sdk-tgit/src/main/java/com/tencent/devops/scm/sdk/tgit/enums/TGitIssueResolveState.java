package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 采纳状态
 */
public enum TGitIssueResolveState {
    ACCEPTED("accepted"),
    RESOLVED("resolved"),
    DENIED("denied");

    public final String value;

    TGitIssueResolveState(String value) {
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
