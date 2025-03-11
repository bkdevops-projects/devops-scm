package com.tencent.devops.scm.sdk.tgit.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TGitProjectStatistics {
    @JsonProperty("commit_count")
    private Integer commitCount;
    @JsonProperty("repository_size")
    private Integer repositorySize;
    @JsonProperty("lfs_repository_size")
    private Integer lfsRepositorySize;
}
