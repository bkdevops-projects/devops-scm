package com.tencent.devops.scm.sdk.gitlab.pojo.webhook;

import java.util.List;
import lombok.Data;

@Data
public class GitlabPushEvent {
    private String objectKind;
    private String eventType;
    private String before;
    private String after;
    private String ref;
    private String checkoutSha;
    private Long userId;
    private String userName;
    private String userUsername;
    private String userEmail;
    private String userAvatar;
    private Long projectId;
    private GitlabEventRepository project;
    private GitlabEventRepository repository;
    private List<GitlabEventCommit> commits;
    private Integer totalCommitsCount;
}
