package com.tencent.devops.scm.api.enums;

import lombok.Getter;

public enum Visibility {

    UNDEFINEd(0),
    PUBLIC(1),
    INTERNAL(2),
    PRIVATE(3);

    @Getter
    private int level;

    Visibility(int level) {
        this.level = level;
    }
}
