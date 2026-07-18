package com.tencent.devops.scm.sdk.gitlab.pojo.webhook;

import lombok.Data;

@Data
public class GitlabEventRepository {
    private Long id;
    private String name;
    private String pathWithNamespace;
    private String defaultBranch;
    private String gitHttpUrl;
    private String httpUrl;
    private String httpUrlToRepo;
    private String gitSshUrl;
    private String sshUrl;
    private String sshUrlToRepo;
    private String webUrl;
    private String homepage;
    private String url;
}
