package com.tencent.devops.scm.sdk.gitlab.pojo.webhook;

import java.util.List;
import lombok.Data;

@Data
public class GitlabMergeRequestEvent {
    private String objectKind;
    private String eventType;
    private String oldrev;
    private Long projectId;
    private GitlabEventUser user;
    private GitlabEventRepository project;
    private GitlabEventRepository repository;
    private ObjectAttributes objectAttributes;

    @Data
    public static class ObjectAttributes {
        private Long id;
        private Integer iid;
        private String action;
        private String state;
        private String title;
        private String description;
        private String sourceBranch;
        private String targetBranch;
        private Long sourceProjectId;
        private Long targetProjectId;
        private Long authorId;
        private GitlabEventUser author;
        private String createdAt;
        private String updatedAt;
        private String url;
        private String webUrl;
        private String sha;
        private String oldrev;
        private String mergeCommitSha;
        private String squashCommitSha;
        private GitlabEventRepository source;
        private GitlabEventRepository target;
        private GitlabEventCommit lastCommit;
        private List<GitlabEventLabel> labels;
    }
}
