package com.tencent.devops.scm.sdk.bkcode.enums;

import lombok.Getter;

/**
 * bkcode review事件动作类型
 */
@Getter
public enum BkCodeReviewAction {
    REVIEW_REQUEST("review_request"),
    REVIEW_REQUEST_REMOVED("review_request_removed"),
    SUBMIT_REVIEW("submit_review");

    /**
     * webhook事件标识
     */
    private final String value;

    BkCodeReviewAction(String value) {
        this.value = value;
    }
}
