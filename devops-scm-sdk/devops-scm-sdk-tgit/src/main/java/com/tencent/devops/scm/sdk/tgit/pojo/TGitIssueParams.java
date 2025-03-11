package com.tencent.devops.scm.sdk.tgit.pojo;

import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueResolveState;
import com.tencent.devops.scm.sdk.tgit.enums.TGitStateEvent;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TGitIssueParams {

    private String title;
    private Integer grade;
    private String description;
    private List<Long> assigneeIds;
    private Integer milestoneId;
    private List<String> labels;
    private TGitIssueResolveState resolveState;
    private TGitStateEvent stateEvent;
}
