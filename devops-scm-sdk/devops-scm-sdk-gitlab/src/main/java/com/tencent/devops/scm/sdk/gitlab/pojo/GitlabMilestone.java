package com.tencent.devops.scm.sdk.gitlab.pojo;

import java.util.Date;
import lombok.Data;

@Data
public class GitlabMilestone {
    private Long id;
    private Long iid;
    private String title;
    private String description;
    private String state;
    private Date createdAt;
    private Date updatedAt;
    private Date dueDate;
}
