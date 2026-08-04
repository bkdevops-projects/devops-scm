package com.tencent.devops.scm.sdk.gitlab.pojo;

import lombok.Data;

@Data
public class GitlabProject {
    private Long id;
    private String name;
    private String path;
    private String pathWithNamespace;
    private String description;
    private String defaultBranch;
    private Boolean archived;
    private String visibility;
    private String webUrl;
    private String httpUrlToRepo;
    private String sshUrlToRepo;
    private String avatarUrl;
    private GitlabNamespace namespace;
}
