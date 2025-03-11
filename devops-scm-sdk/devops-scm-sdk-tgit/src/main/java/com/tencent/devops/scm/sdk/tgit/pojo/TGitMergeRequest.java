package com.tencent.devops.scm.sdk.tgit.pojo;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class TGitMergeRequest {
    private Long id;
    private Integer iid;
    private String title;
    private Long targetProjectId;
    private String targetBranch;
    private Long sourceProjectId;
    private String sourceBranch;
    private String state;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private List<String> labels;
    private TGitAssignee assignee;
    private TGitAuthor author;
    private TGitMilestone milestone;
    private List<TGitDiff> files;
    private Long projectId;
    private Boolean workInProgress;
    private Integer upvotes;
    private Integer downvotes;

    private String baseCommit;
    private String targetCommit;
    private String sourceCommit;
    private String mergeCommitSha;

    private String mergeStatus;
    private String commitCheckState;
    private Boolean commitCheckBlock;
}
