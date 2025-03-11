package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
public class TGitEventMergeRequest {
    private Long id;
    @JsonProperty("target_branch")
    private String targetBranch;
    @JsonProperty("source_branch")
    private String sourceBranch;
    @JsonProperty("source_project_id")
    private Long sourceProjectId;
    @JsonProperty("author_id")
    private Long authorId;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    private String title;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("updated_at")
    private Date updatedAt;
    @JsonProperty("st_commits")
    private String stCommits;
    @JsonProperty("st_diffs")
    private String stDiffs;
    @JsonProperty("milestone_id")
    private Long milestoneId;
    private String state;
    @JsonProperty("merge_status")
    private String mergeStatus;
    @JsonProperty("target_project_id")
    private Long targetProjectId;
    private Integer iid;
    private String description;
    private TGitEventProject source;
    private TGitEventProject target;
    private TGitEventCommit lastCommit;
    @JsonProperty("merge_type")
    private String mergeType;
    @JsonProperty("merge_commit_sha")
    private String mergeCommitSha;
    private String before;
    private String after;
    private String url;
    private String action;
    @JsonProperty("extension_action")
    private String extensionAction;
    // TODO 补充labels字段
}
