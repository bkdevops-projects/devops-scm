package com.tencent.devops.scm.sdk.gitee.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseUser;
import java.util.List;
import lombok.Data;

@Data
public class GiteePushHook {

    private GiteeBaseUser pusher;
    private String compare;
    @JsonProperty("head_commit")
    private GiteeEventCommit headCommit;
    @JsonProperty("total_commits_count")
    private Long totalCommitsCount;
    private String before;
    private String userName;
    private String sign;
    private GiteeEventRepository project;
    private GiteeEventRepository repository;
    private String ref;
    private String password;
    private String after;
    @JsonProperty("hook_url")
    private String hookUrl;
    private String timestamp;
    @JsonProperty("hook_name")
    private String hookName;
    @JsonProperty("user_email")
    private String userEmail;
    private Boolean created;
    @JsonProperty("commits_more_than_ten")
    private Boolean commitsMoreThanTen;
    private Boolean deleted;
    private GiteeBaseUser sender;
    private Long userId;
    private Long hookId;
    private List<GiteeEventCommit> commits;
    private GiteeBaseUser user;
}
