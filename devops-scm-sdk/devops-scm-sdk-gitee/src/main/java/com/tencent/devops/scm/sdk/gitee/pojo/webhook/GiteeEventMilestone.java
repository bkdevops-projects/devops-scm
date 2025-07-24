package com.tencent.devops.scm.sdk.gitee.pojo.webhook;

import java.util.Date;
import lombok.Data;

@Data
public class GiteeEventMilestone {
    private Date createdAt;
    private String description;
    private String title;
    private Long closedIssues;
    private String dueOn;
    private Long number;
    private Date updatedAt;
    private String htmlUrl;
    private Long id;
    private String state;
    private Long openIssues;
}