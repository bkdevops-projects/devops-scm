package com.tencent.devops.scm.sdk.gitee.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GiteePullRequestRef extends GiteeBaseRef {
    private GiteeBaseRepository repo;

    private GiteeBaseUser user;
}
