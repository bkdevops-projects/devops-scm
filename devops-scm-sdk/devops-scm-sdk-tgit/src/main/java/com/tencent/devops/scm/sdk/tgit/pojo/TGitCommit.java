package com.tencent.devops.scm.sdk.tgit.pojo;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class TGitCommit {

    private String id;
    private String message;
    private List<String> parentIds;
    private Date authoredDate;
    private String authorEmail;
    private String authorName;
    private Date committedDate;
    private String committerEmail;
    private String committerName;
    private Date createdAt;
    private String title;
    private String shortId;
}
