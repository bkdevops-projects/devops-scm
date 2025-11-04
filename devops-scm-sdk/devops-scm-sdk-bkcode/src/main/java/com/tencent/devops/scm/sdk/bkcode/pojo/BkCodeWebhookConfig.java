package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * BkCode Webhook配置信息
 */
@Data
public class BkCodeWebhookConfig {
    /**
     * webhook ID
     */
    private Integer id;
    /**
     * webhook名称
     */
    private String name;
    /**
     * webhook URL
     */
    private String url;
    /**
     * webhook密钥token
     */
    private String token;
    /**
     * 是否启用SSL验证
     */
    @JsonProperty("sslVerificationEnabled")
    private boolean sslVerificationEnabled;
    /**
     * 关联的事件类型列表
     */
    private List<String> events;
    /**
     * 是否启用
     */
    private boolean enabled;
    /**
     * 分支过滤正则表达式
     */
    @JsonProperty("branchPattern")
    private String branchPattern;
    /**
     * 最后触发时间
     */
    @JsonProperty("lastTriggeredAt")
    private Date lastTriggeredAt;
    /**
     * 最后触发状态
     */
    @JsonProperty("lastTriggeredStatus")
    private String lastTriggeredStatus;
    /**
     * 描述信息
     */
    private String description;
    /**
     * 所属对象ID
     */
    @JsonProperty("ownerId")
    private String ownerId;
    /**
     * 创建人
     */
    private BkCodeUser creator;
    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    private Date createTime;
    /**
     * 更新人
     */
    private BkCodeUser updater;
    /**
     * 更新时间
     */
    @JsonProperty("updateTime")
    private Date updateTime;
}
