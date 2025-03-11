package com.tencent.devops.scm.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusState {
    UNKNOWN("unknown"),
    PENDING("pending"),
    RUNNING("running"),
    SUCCESS("success"),
    FAILURE("failure"),
    CANCELED("canceled"),
    ERROR("error")
    ;
    public final String value;

    StatusState(String value) {
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
