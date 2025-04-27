package com.tencent.devops.scm.sdk.gitee.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GiteeEventAuthor extends GiteeBaseUser {
    @JsonProperty("avatar_url")
    private String avatarUrl;
    private String username;
    @JsonProperty("html_url")
    private String htmlUrl;
    private String name;
    @JsonProperty("site_admin")
    private Boolean siteAdmin;
    private Long id;
    private String email;
}
