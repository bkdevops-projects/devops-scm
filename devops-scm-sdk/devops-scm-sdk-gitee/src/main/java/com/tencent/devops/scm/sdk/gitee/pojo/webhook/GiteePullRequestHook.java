package com.tencent.devops.scm.sdk.gitee.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class GiteePullRequestHook {
    private Long iid;
    private String sign;
    private GiteeEventRepository project;
    private String body;
    private String title;
    private GiteeEventRepository repository;
    private String sourceBranch;
    private Long number;
    private String password;
    @JsonProperty("merge_commit_sha")
    private String mergeCommitSha;
    private String action;
    private String state;
    @JsonProperty("hook_url")
    private String hookUrl;
    private GiteeEventRepoInfo targetRepo;
    private String timestamp;
    private String hookName;
    private GiteeEventPullRequest pullRequest;
    private List<Object> languages;
    private String actionDesc;
    private GiteeEventRepoInfo sourceRepo;
    private GiteeEventAuthor author;
    @JsonProperty("target_branch")
    private String targetBranch;
    private String url;
    private GiteeEventAuthor sender;
    @JsonProperty("merge_status")
    private String mergeStatus;
    private Long hookId;
    private GiteeEventAuthor updatedBy;
}

