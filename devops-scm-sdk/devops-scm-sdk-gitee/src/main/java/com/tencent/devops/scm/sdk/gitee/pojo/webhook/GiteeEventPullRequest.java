package com.tencent.devops.scm.sdk.gitee.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseLabel;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class GiteeEventPullRequest {
    private String mergeReferenceName;
    private Long deletions;
    @JsonProperty("diff_url")
    private String diffUrl;
    private List<GiteeEventAuthor> assignees;
    private Date createdAt;
    private String body;
    private String title;
    private List<Object> issues;
    private GiteeEventRef head;
    private Long number;
    @JsonProperty("mergeable")
    private Boolean mergeable;
    @JsonProperty("patch_url")
    private String patchUrl;
    @JsonProperty("need_review")
    private Boolean needReview;
    private Date updatedAt;
    @JsonProperty("merge_commit_sha")
    private String mergeCommitSha;
    private Long id;
    private String state;
    @JsonProperty("stale_labels")
    private List<GiteeBaseLabel> staleLabels;
    @JsonProperty("milestone")
    private GiteeEventMilestone milestone;
    @JsonProperty("testers")
    private List<GiteeEventAuthor> testers;
    private Long comments;
    private Long additions;
    private List<Object> languages;
    private Boolean merged;
    private Long changedFiles;
    private List<GiteeBaseLabel> labels;
    @JsonProperty("need_test")
    private Boolean needTest;
    @JsonProperty("merge_status")
    private String mergeStatus;
    @JsonProperty("html_url")
    private String htmlUrl;
    private GiteeEventAuthor updatedBy;
    private Long commits;
    private GiteeEventAuthor user;
    private GiteeEventRef base;
    @JsonProperty("stale_issues")
    private List<Object> staleIssues;
}