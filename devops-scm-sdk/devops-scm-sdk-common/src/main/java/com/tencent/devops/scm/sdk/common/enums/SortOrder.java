package com.tencent.devops.scm.sdk.common.enums;

import lombok.Getter;

@Getter
public enum SortOrder {
    ASC("asc"),
    DESC("desc");

    private String value;

    SortOrder(String value) {
        this.value = value;
    }
}