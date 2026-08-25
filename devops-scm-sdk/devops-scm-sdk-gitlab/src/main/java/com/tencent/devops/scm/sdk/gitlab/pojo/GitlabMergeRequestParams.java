package com.tencent.devops.scm.sdk.gitlab.pojo;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GitlabMergeRequestParams {
    private String sourceBranch;
    private String targetBranch;
    private String title;
    private String description;
    private Long assigneeId;
    private List<Long> assigneeIds;
    private List<String> labels;
    private Long milestoneId;
    private Long targetProjectId;
    private Boolean removeSourceBranch;
    private Boolean squash;
    private String stateEvent;
}
