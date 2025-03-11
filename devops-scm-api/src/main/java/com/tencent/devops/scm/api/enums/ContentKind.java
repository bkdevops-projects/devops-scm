package com.tencent.devops.scm.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ContentKind {
    FILE("file"),
    DIRECTORY("directory"),
    SYMLINK("symlink"),
    GITLINK("gitlink"),
    UNSUPPORTED("unsupported")
    ;

    public final String value;

    ContentKind(String value) {
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
