package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

/**
 * BkCode WebHook 事件抽象类
 */
@Data
public abstract class BkCodeEvent {
    /**
     * 事件发生时间，ISO 8601格式
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS", timezone = "GMT+8")
    @JsonProperty("createdAt")
    private Date createdAt;
    /**
     * 触发事件的用户信息
     */
    private BkCodeEventUser sender;
    /**
     * 具体动作，如"created", "edited", "merged"等
     */
    private String action;
    /**
     * 事件类型，如"push", "merge_request", "issues"等
     */
    private String event;
    /**
     * 关联的仓库信息（部分事件可能为null）
     */
    private BkCodeEventRepository repository;
    /**
     * 仓库组
     */
    private BkCodeEventGroup group;
}
