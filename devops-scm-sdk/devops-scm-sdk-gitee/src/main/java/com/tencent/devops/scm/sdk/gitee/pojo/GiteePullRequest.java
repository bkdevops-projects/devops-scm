package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class GiteePullRequest {
    @JsonProperty("issue_url")
    private String issueUrl;
    @JsonProperty("ref_pull_requests")
    private List<GiteePullRequestRef> refPullRequests;
    @JsonProperty("diff_url")
    private String diffUrl;
    private List<GiteeAssigne> assignees;
    private Date createdAt;
    @JsonProperty("can_merge_check")
    private Boolean canMergeCheck;
    private String title;
    private String body;
    private GiteePullRequestRef head;
    private Long number;
    private Boolean mergeable;
    @JsonProperty("patch_url")
    private String patchUrl;
    @JsonProperty("updated_at")
    private Date updatedAt;
    @JsonProperty("testers_number")
    private Long testersNumber;
    private Boolean draft;
    @JsonProperty("comments_url")
    private String commentsUrl;
    @JsonProperty("review_comment_url")
    private String reviewCommentUrl;
    private Long id;
    private String state;
    private Boolean locked;
    @JsonProperty("commits_url")
    private String commitsUrl;
    @JsonProperty("close_related_issue")
    private Long closeRelatedIssue;
    private List<GiteeAssigne> testers;
    @JsonProperty("closed_at")
    private Date closedAt;
    @JsonProperty("prune_branch")
    private Boolean pruneBranch;
    private String url;
    private List<GiteeBaseLabel> labels;
    private GiteeBaseMilestone milestone;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("review_comments_url")
    private String reviewCommentsUrl;
    @JsonProperty("assignees_number")
    private Long assigneesNumber;
    private GiteeBaseUser user;
    private GiteePullRequestRef base;
}
