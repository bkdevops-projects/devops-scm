package com.tencent.devops.scm.sdk.gitlab.pojo;

import lombok.Data;

@Data
public class GitlabDiffRefs {
    private String baseSha;
    private String headSha;
    private String startSha;
}
