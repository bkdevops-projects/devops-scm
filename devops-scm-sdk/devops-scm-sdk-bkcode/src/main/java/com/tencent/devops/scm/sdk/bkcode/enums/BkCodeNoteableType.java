package com.tencent.devops.scm.sdk.bkcode.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum BkCodeNoteableType {
    COMMIT("Commit"),
    REVIEW("Review"),
    ISSUE("Issue")
    ;

    public final String value;

    BkCodeNoteableType(String value) {
        this.value = value;
    }
}
