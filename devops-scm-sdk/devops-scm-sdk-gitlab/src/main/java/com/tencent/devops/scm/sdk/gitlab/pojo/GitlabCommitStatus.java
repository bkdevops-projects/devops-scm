package com.tencent.devops.scm.sdk.gitlab.pojo;

import java.util.Date;
import lombok.Data;

@Data
public class GitlabCommitStatus {
    private Long id;
    private String sha;
    private String ref;
    private String status;
    private String name;
    private String description;
    private String targetUrl;
    private Date createdAt;
    private Date startedAt;
    private Date finishedAt;
    private GitlabUser author;
}
