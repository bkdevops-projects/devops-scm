package com.tencent.devops.scm.sdk.gitee.pojo.webhook;

import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseRef;
import lombok.Data;

@Data
public class GiteeEventRef extends GiteeBaseRef {
    private GiteeEventRepository repo;
    private GiteeEventAuthor user;
}