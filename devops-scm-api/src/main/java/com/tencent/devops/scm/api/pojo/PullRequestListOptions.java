package com.tencent.devops.scm.api.pojo;

import com.tencent.devops.scm.api.enums.PullRequestState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PullRequestListOptions {

    private PullRequestState state;
    private String sourceBranch;
    private String targetBranch;
    private Integer page;
    private Integer pageSize;
}
