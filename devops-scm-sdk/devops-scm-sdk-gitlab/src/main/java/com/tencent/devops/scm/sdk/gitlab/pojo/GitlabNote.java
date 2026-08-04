package com.tencent.devops.scm.sdk.gitlab.pojo;

import java.util.Date;
import lombok.Data;

@Data
public class GitlabNote {
    private Long id;
    private String body;
    private GitlabUser author;
    private Date createdAt;
    private Date updatedAt;
    private boolean system;
    private String noteableType;
    private Long noteableId;
    private Long projectId;
}
