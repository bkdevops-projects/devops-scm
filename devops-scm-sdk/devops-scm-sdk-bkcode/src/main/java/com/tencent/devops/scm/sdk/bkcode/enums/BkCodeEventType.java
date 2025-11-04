package com.tencent.devops.scm.sdk.bkcode.enums;

import lombok.Getter;

/**
 * bkcode事件类型
 */
@Getter
public enum BkCodeEventType {
    PUSH("push", "PUSH_EVENT"),
    TAG_PUSH("tag_push", "TAG_PUSH_EVENT"),
    MERGE_REQUEST("merge_request", "MERGE_REQUEST_EVENT"),
    NOTE("note", "COMMENT_EVENT"),
    ISSUES("issues","ISSUE_EVENT"),
    REPOSITORY("repository","REPO_EVENT"),
    REVIEW("review","MERGE_REQUEST_EVENT"),
    UNKNOWN("unknown",""),
    ;

    /**
     * webhook事件标识
     */
    private final String value;


    /**
     * Hook事件名称，用于创建和解析bkcode webhook配置
     */
    private final String hookName;

    BkCodeEventType(String value, String hookName) {
        this.value = value;
        this.hookName = hookName;
    }

    public static BkCodeEventType fromValue(String value) {
        for (BkCodeEventType event : BkCodeEventType.values()) {
            if (event.value.equals(value)) {
                return event;
            }
        }
        return UNKNOWN;
    }
}
