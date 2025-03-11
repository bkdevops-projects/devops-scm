package com.tencent.devops.scm.sdk.tgit.pojo;

import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueResolveState;
import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueState;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class TGitIssue {
    private Long id;
    private Integer iid;
    private Long projectId;
    private String title;
    private String description;
    private TGitIssueState state;
    private TGitIssueResolveState resolveState;
    private List<String> labels;
    private TGitMilestone milestone;
    private String priority;
    private TGitAssignee assignee;
    private List<TGitAssignee> assignees;
    private TGitAuthor author;
    // 是否为私密
    private Boolean confidential;
    private Date createdAt;
    private Date updatedAt;
}
