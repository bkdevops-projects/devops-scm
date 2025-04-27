package com.tencent.devops.scm.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 源码管理平台提供者
 */
public enum ScmProviderCodes {
    TGIT("tgit"),
    TSVN("tsvn"),
    GITHUB("github"),
    GITLAB("gitlab"),
    GITEE("gitee"),
    ;
    public final String value;

    ScmProviderCodes(String value) {
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
