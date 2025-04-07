package com.tencent.devops.scm.sdk.gitee.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseRepository;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GiteeEventRepository extends GiteeBaseRepository {
    @JsonProperty("stargazers_count")
    private Long stargazersCount;
    @JsonProperty("pushed_at")
    private Date pushedAt;
    @JsonProperty("open_issues_count")
    private Long openIssuesCount;
    private Date createdAt;
    private String language;
    @JsonProperty("git_http_url")
    private String gitHttpUrl;
    @JsonProperty("git_ssh_url")
    private String gitSshUrl;
    private Boolean hasWiki;
    private Date updatedAt;
    @JsonProperty("git_svn_url")
    private String gitSvnUrl;
    private String svnUrl;
    @JsonProperty("git_url")
    private String gitUrl;
    private Boolean hasPages;
    @JsonProperty("path_with_namespace")
    private String pathWithNamespace;
    private Boolean hasIssues;
    @JsonProperty("clone_url")
    private String cloneUrl;
    private String namespace;
    @JsonProperty("default_branch")
    private String defaultBranch;
    @JsonProperty("watchers_count")
    private Long watchersCount;
    @JsonProperty("name_with_namespace")
    private String nameWithNamespace;
    @JsonProperty("forks_count")
    private Long forksCount;
}