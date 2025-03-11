package com.tencent.devops.scm.sdk.tgit.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/*
 * operation_kind字段
 * create: 创建分支、合并普通MR的push
 * delete: 删除分支的push
 * update: 文件修改的push
 * update_nonfastword: force push
 */
public enum TGitPushOperationKind {
    CREAT("create"),
    DELETE("delete"),
    UPDATE("update"),
    UPDATE_NONFASTFORWORD("update_nonfastforward");


    public final String value;

    TGitPushOperationKind(String value) {
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
