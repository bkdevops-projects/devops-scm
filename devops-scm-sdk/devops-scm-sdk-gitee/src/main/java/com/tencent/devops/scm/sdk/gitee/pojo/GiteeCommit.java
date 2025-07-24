package com.tencent.devops.scm.sdk.gitee.pojo;

import lombok.Data;

@Data
public class GiteeCommit {
    private GiteeBaseUser committer;
    private GiteeBaseUser author;
    private String message;
}
