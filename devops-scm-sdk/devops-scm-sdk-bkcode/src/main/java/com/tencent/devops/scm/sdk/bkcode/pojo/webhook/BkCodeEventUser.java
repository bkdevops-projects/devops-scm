package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeBaseUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BkCodeEventUser  extends BkCodeBaseUser {
    private String id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 显示名称
     */
    @JsonProperty("displayName")
    private String displayName;
    /**
     * 头像URL
     */
    @JsonProperty("avatarUrl")
    private String avatarUrl;
}