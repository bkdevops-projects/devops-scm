package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class GiteeCommitCompare {

    @JsonProperty("base_commit")
    GiteeBranchCommit baseCommit;

    @JsonProperty("merge_base_commit")
    GiteeBranchCommit mergeBaseCommit;

    List<GiteeBranchCommit> commits;

    List<GiteeFileChange> files;

    Boolean truncated;
}
