package com.tencent.devops.scm.sdk.tgit.pojo;

import java.util.Date;
import lombok.Data;

@Data
public class TGitMilestone {
    private Integer id;
    private Integer iid;
    private String title;
    private String description;
    private Integer projectId;
    private String state;
    private Date dueDate;
    private Date createdAt;
    private Date updatedAt;
}
