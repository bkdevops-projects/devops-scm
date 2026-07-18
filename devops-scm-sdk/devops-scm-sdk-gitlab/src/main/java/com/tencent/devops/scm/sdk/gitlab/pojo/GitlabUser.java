package com.tencent.devops.scm.sdk.gitlab.pojo;

import lombok.Data;

@Data
public class GitlabUser {
    private Long id;
    private String username;
    private String name;
    private String state;
    private String avatarUrl;
    private String webUrl;
    private String email;
}
