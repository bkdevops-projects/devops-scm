package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
public class GiteeUserInfo extends GiteeBaseUser {
    private String login;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
}
