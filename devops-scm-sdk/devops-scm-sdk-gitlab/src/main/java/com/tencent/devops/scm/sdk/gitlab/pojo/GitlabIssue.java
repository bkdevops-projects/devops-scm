package com.tencent.devops.scm.sdk.gitlab.pojo;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class GitlabIssue {
    private Long id;
    private Long iid;
    private Long projectId;
    private String title;
    private String description;
    private String state;
    private GitlabUser author;
    private GitlabUser assignee;
    private List<GitlabUser> assignees;
    private List<String> labels;
    private GitlabMilestone milestone;
    private Date createdAt;
    private Date updatedAt;
    private Date closedAt;
    private String webUrl;
}
