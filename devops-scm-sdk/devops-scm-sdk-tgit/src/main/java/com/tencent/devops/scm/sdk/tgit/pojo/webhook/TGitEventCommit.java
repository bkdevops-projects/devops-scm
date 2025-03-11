package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.tencent.devops.scm.sdk.tgit.pojo.TGitAuthor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TGitEventCommit {
    private String id;
    private String message;
    private Date timestamp;
    private String url;
    private TGitAuthor author;
    private List<String> added;
    private List<String> modified;
    private List<String> removed;
}
