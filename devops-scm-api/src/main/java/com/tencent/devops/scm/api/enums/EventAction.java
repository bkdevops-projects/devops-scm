package com.tencent.devops.scm.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 事件动作
 */
public enum EventAction {
    UNKNOWN("unknown"),
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),

    OPEN("open"),
    REOPEN("reopen"),
    CLOSE("close"),
    MERGE("merge"),
    // 源分支变更提交
    PUSH_UPDATE("push-update"),

    /* git push action */
    // 创建分支
    NEW_BRANCH("new-branch"),
    // 变更文件
    PUSH_FILE("push-file"),
    // 编辑
    EDIT("edit"),
    ;
    public final String value;

    EventAction(String value) {
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
