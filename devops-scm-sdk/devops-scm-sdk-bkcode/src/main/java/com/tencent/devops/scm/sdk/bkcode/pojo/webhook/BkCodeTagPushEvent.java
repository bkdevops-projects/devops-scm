package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BkCodeTagPushEvent extends BkCodeEvent {

    /**
     * 推送人
     */
    private BkCodeEventUser pusher;
    /**
     * 是否创建了新标签
     */
    private Boolean created;
    /**
     * 标签名称
     */
    @JsonProperty("tagName")
    private String tagName;
    /**
     * 标签引用，例如 "refs/tags/v1.0.0"
     */
    private String ref;
    /**
     * 是否删除了标签
     */
    private Boolean deleted;
    /**
     * 为"0000000000000000000000000000000000000000"表示删除标签
     */
    private String after;
    /**
     * 为"0000000000000000000000000000000000000000"表示新建标签
     */
    private String before;
    /**
     * 标签详情
     */
    private BkCodeEventTag tag;
}
