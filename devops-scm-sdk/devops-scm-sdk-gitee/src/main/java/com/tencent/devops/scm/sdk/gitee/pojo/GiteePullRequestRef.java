package com.tencent.devops.scm.sdk.gitee.pojo;

import lombok.Data;

@Data
public class GiteePullRequestRef extends GiteeBaseRef {
    private GiteeBaseRepository repo;

    private GiteeBaseUser user;
}
