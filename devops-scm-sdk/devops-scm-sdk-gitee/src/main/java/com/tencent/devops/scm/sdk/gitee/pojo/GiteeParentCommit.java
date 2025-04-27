package com.tencent.devops.scm.sdk.gitee.pojo;

import lombok.Data;

@Data
public class GiteeParentCommit {
    private String sha;
    private String url;
}