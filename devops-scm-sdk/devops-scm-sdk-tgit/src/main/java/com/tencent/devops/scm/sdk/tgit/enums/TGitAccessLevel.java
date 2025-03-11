package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TGitAccessLevel {

    EMPTY(0),
    GUEST(10),
    FOLLOWER(15),
    REPORTER(20),
    DEVELOPER(30),
    MASTER(40),
    OWNER(50);

    public final Integer value;

    TGitAccessLevel(int value) {
        this.value = value;
    }

    @JsonValue
    public Integer toValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
