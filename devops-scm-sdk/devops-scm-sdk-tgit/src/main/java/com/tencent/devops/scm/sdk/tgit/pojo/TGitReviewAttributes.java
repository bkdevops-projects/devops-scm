package com.tencent.devops.scm.sdk.tgit.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventRepository;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TGitReviewAttributes {
    private Long id;
    @JsonProperty("source_commit")
    private String sourceCommit;
    @JsonProperty("source_branch")
    private String sourceBranch;
    @JsonProperty("source_project_id")
    private String sourceProjectId;
    @JsonProperty("target_commit")
    private String targetCommit;
    @JsonProperty("target_branch")
    private String targetBranch;
    @JsonProperty("target_project_id")
    private String targetProjectId;
    @JsonProperty("author_id")
    private String authorId;
    @JsonProperty("assignee_id")
    private String assigneeId;
    private String title;
    @JsonProperty("commit_check_state")
    private String commitCheckState;
    @JsonProperty("updated_by_id")
    private String updatedById;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    private String state;
    private Long iid;
    private String description;
    private TGitEventRepository source;
    private TGitEventRepository target;
    @JsonProperty("last_commit")
    private TGitCommit lastCommit;
}