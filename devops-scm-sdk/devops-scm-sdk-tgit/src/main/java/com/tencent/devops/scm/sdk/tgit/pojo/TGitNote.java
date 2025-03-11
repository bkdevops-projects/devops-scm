package com.tencent.devops.scm.sdk.tgit.pojo;

import java.util.Date;
import lombok.Data;

@Data
public class TGitNote {
    private Long id;
    private Long parentId;
    private String body;
    private TGitAuthor author;
    private Date createAt;
    private Boolean system;
    private String attachment;
    private Boolean downvote;
    private Boolean upvote;
}
