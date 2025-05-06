package com.tencent.devops.scm.sdk.gitee.pojo;

import lombok.Data;

@Data
public class GiteeBranchCommit {
    private String url;
    private String sha;
    private GiteeCommit commit;
}