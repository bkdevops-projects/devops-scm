package com.tencent.devops.scm.sdk.gitlab.pojo;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class GitlabCommit {
    private String id;
    private String shortId;
    private String title;
    private String message;
    private String authorName;
    private String authorEmail;
    private String committerName;
    private String committerEmail;
    private Date authoredDate;
    private Date committedDate;
    private Date createdAt;
    private List<String> parentIds;
    private String webUrl;
}
