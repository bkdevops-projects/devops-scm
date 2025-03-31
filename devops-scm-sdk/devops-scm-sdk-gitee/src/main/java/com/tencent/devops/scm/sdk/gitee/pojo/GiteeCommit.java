package com.tencent.devops.scm.sdk.gitee.pojo;

import lombok.Data;

@Data
public class GiteeCommit {
    private GiteeAuthor committer;
    private GiteeAuthor author;
    private String message;
}
