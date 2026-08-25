package com.tencent.devops.scm.sdk.gitlab.pojo.webhook;

import lombok.Data;

@Data
public class GitlabNoteEvent {
    private String objectKind;
    private String eventType;
    private Long projectId;
    private GitlabEventUser user;
    private GitlabEventRepository project;
    private GitlabEventRepository repository;
    private ObjectAttributes objectAttributes;
    private GitlabEventCommit commit;
    private GitlabIssueEvent.ObjectAttributes issue;
    private GitlabMergeRequestEvent.ObjectAttributes mergeRequest;

    @Data
    public static class ObjectAttributes {
        private Long id;
        private String action;
        private String note;
        private String noteableType;
        private String createdAt;
        private String updatedAt;
        private String url;
    }
}
