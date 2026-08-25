package com.tencent.devops.scm.sdk.gitlab.pojo;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class GitlabMergeRequest {
    private Long id;
    private Long iid;
    private Long projectId;
    private String title;
    private String description;
    private String state;
    private String sourceBranch;
    private String targetBranch;
    private Long sourceProjectId;
    private Long targetProjectId;
    private GitlabUser author;
    private GitlabUser assignee;
    private List<GitlabUser> assignees;
    private List<GitlabUser> reviewers;
    private List<String> labels;
    private GitlabMilestone milestone;
    private Date createdAt;
    private Date updatedAt;
    private Date mergedAt;
    private Date closedAt;
    private String mergeCommitSha;
    private String squashCommitSha;
    private String sha;
    private GitlabDiffRefs diffRefs;
    private String webUrl;
    private List<GitlabDiff> changes;
    private Boolean overflow;
}
