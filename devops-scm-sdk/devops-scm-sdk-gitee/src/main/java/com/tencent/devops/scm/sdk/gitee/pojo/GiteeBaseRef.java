package com.tencent.devops.scm.sdk.gitee.pojo;

import lombok.Data;

@Data
public class GiteeBaseRef {
    private String ref;
    private String label;
    private String sha;
}