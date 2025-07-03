package com.tencent.devops.scm.api.enums

enum class EventAction(val value: String) {
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
    NEW_BRANCH_AND_PUSH_FILE("new-branch-and-push-file"),
    ;
}
