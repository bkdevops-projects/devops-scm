package com.tencent.devops.scm.sdk.bkcode.enums;

import lombok.Getter;

/**
 * bkcode merge request 事件状态
 */
@Getter
public enum BkCodeMergeRequestStatus {
    CANNOT_BE_MERGED("cannot_be_merged"),
    CAN_BE_MERGED("can_be_merged");

    /**
     * webhook事件标识
     */
    private final String value;

    BkCodeMergeRequestStatus(String value) {
        this.value = value;
    }
}
