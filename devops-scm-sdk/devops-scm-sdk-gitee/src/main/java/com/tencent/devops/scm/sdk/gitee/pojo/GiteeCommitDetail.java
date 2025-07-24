package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class GiteeCommitDetail {
    private GiteeBaseUser committer;
    private GiteeBaseUser author;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("comments_url")
    private String commentsUrl;
    private GiteeCommit commit;
    private List<GiteeFileChange> files;
    private Boolean truncated;
    private String sha;
    private String url;
    private List<GiteeParentCommit> parents;
}
