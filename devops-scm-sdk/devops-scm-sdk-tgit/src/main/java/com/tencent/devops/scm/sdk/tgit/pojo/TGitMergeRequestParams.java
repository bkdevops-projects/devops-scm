package com.tencent.devops.scm.sdk.tgit.pojo;

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
public class TGitMergeRequestParams {

    private String sourceBranch;
    private String targetBranch;
    private String title;
    private Integer assigneeId;
    private String description;
    private Long targetProjectId;
    private List<String> labels;
    // 评审人 id (只能是 id。多个评审人请用英文逗号分隔)
    private List<Long> reviewers;
    // 必要评审人 id (只能是 id。多个评审人请用英文逗号分隔)
    private List<Long> necessaryReviewers;
    // 评审人规则
    private Integer approverRule;
    // 必要评审人规则
    private Integer necessaryApproverRule;
    private TGitStateEvent stateEvent;
}
