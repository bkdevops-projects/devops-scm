package com.tencent.devops.scm.sdk.gitlab.pojo.webhook;

import lombok.Data;

@Data
public class GitlabEventUser {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String avatarUrl;
}
