package com.tencent.devops.scm.sdk.gitee.pojo.webhook;

import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseRef;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GiteeEventRef extends GiteeBaseRef {
    private GiteeEventRepository repo;
    private GiteeEventAuthor user;
}