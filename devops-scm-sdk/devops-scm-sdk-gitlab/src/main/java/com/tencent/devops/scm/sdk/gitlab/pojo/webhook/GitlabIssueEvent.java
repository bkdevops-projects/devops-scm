package com.tencent.devops.scm.sdk.gitlab.pojo.webhook;

import java.util.List;
import lombok.Data;

@Data
public class GitlabIssueEvent {
    private String objectKind;
    private String eventType;
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
        private Long authorId;
        private GitlabEventUser author;
        private String createdAt;
        private String updatedAt;
        private String url;
        private String webUrl;
        private Long milestoneId;
        private List<GitlabEventLabel> labels;
    }
}
