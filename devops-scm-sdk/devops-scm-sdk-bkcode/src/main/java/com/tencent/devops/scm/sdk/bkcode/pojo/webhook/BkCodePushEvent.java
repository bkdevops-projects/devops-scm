package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommit;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitDetail;
import java.util.List;
import lombok.Data;

import lombok.EqualsAndHashCode;

/**
 * PUSH 事件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BkCodePushEvent extends BkCodeEvent {
    private BkCodeEventUser pusher;
    /**
     * 比较变更的URL
     */
    private String compare;
    /**
     * 推送前的提交ID
     */
    private String before;
    /**
     * 推送后的提交ID
     */
    private String after;
    /**
     * 是否为强制推送
     */
    private Boolean forced;
    /**
     * 是否创建了新分支
     */
    private Boolean created;
    /**
     * 是否删除了分支
     */
    private Boolean deleted;
    /**
     * 推送的头部提交
     */
    @JsonProperty("headCommit")
    private BkCodeEventCommit headCommit;
    /**
     * 推送的引用，例如 "refs/heads/main"
     */
    private String ref;
    /**
     * 本次推送包含的提交列表
     */
    private List<BkCodeEventCommit> commits;
}

