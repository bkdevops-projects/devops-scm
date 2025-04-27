package com.tencent.devops.scm.sdk.gitee.pojo;

import lombok.Data;

@Data
public class GiteeRefPullRequest {
    private String number;
    private String state;
    private String title;
}
