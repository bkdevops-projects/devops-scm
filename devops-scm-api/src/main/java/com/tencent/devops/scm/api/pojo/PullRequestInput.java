package com.tencent.devops.scm.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PullRequestInput {

    private String title;
    private String body;
    private String sourceBranch;
    private String targetBranch;
    private Object sourceRepo;
    private Object targetRepo;
}
