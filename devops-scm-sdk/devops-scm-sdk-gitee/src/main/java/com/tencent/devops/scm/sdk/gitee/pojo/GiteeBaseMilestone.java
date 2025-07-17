package com.tencent.devops.scm.sdk.gitee.pojo;

import java.util.Date;
import lombok.Data;

@Data
public class GiteeBaseMilestone {
    private Date createdAt;
    private String description;
    private String title;
    private String dueOn;
    private Long number;
    private Date updatedAt;
    private String htmlUrl;
    private Long id;
    private String state;
}