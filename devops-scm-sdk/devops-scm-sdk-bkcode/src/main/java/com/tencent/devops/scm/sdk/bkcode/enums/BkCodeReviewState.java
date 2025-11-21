package com.tencent.devops.scm.sdk.bkcode.enums;

import lombok.Getter;

/**
 * bkcode review事件状态
 */
@Getter
public enum BkCodeReviewState {
    APPROVED, // 已通过
    REJECTED, // 已拒绝
    PENDING, //评审中
    COMMENTED; // 仅评论
}
