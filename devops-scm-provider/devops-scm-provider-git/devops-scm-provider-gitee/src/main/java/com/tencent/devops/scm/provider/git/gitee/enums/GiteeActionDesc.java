package com.tencent.devops.scm.provider.git.gitee.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GiteeActionDesc {
    SOURCE_BRANCH_CHANGED("source_branch_changed"),
    TARGET_BRANCH_CHANGED("target_branch_changed");

    private final String value;

    @JsonValue
    public String toValue() {
        return value;
    }

    GiteeActionDesc(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
