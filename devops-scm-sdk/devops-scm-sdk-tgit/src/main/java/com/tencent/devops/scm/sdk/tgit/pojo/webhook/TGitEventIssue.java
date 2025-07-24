package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TGitEventIssue {

    private Long id;
    private String title;
    private String description;
    private Integer iid;
    private String url;
    private String action;
    private String state;

    private Long authorId;
    private Long projectId;

    private Date createdAt;
    private Date updatedAt;
    private Integer position;
    @JsonProperty("milestone_id")
    String milestoneId;
}
