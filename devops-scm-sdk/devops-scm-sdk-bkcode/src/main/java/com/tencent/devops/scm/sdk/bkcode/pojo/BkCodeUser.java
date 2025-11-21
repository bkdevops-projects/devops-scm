package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BkCodeUser extends BkCodeBaseUser {
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户 ID
     */
    private String id;
    /**
     * 显示名称
     */
    @JsonProperty("displayName")
    private String displayName;
    /**
     * 租户 ID
     */
    @JsonProperty("tenantId")
    private String tenantId;
    /**
     * 是否管理员
     */
    private Boolean admin;
    /**
     * 语言
     */
    private String locale;
    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    private Date createTime;
}
