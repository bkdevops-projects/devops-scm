package com.tencent.devops.scm.sdk.gitee.pojo.webhook;

import lombok.Data;

@Data
public class GiteeEventRepoInfo {
    private GiteeEventRepository project;
    private GiteeEventRepository repository;
}
