package com.tencent.devops.scm.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 提供者类型
 */
public enum ScmProviderType {
    GIT("git"),
    SVN("svn"),
    P4("p4")
    ;
    public final String value;

    ScmProviderType(String value) {
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
