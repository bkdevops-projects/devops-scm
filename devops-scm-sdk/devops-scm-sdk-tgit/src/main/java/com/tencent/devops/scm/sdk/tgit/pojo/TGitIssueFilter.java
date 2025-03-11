package com.tencent.devops.scm.sdk.tgit.pojo;

import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueOrderBy;
import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueState;
import com.tencent.devops.scm.sdk.tgit.enums.TGitSortOrder;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TGitIssueFilter {
    private Long iid;
    private TGitIssueState state;
    private List<String> labels;
    private String milestone;
    private TGitIssueOrderBy orderBy;
    private TGitSortOrder sort;
    private Date createdAfter;
    private Date createdBefore;
    private Integer page;
    private Integer perPage;
}
