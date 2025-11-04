package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GiteeUserInfo extends GiteeBaseUser {
    private String login;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
}
