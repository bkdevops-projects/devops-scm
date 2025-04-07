package com.tencent.devops.scm.sdk.gitee.pojo.webhook;

import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseUser;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class GiteeEventCommit {
    private List<String> removed;
    private GiteeBaseUser committer;
    private List<String> added;
    private String treeId;
    private GiteeBaseUser author;
    private Boolean distinct;
    private List<String> modified;
    private String id;
    private String message;
    private List<String> parentIds;
    private String url;
    private Date timestamp;
}