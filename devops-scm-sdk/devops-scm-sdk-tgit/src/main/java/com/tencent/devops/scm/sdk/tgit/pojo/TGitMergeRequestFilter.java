package com.tencent.devops.scm.sdk.tgit.pojo;

import com.tencent.devops.scm.sdk.tgit.enums.TGitMergeRequestOrderBy;
import com.tencent.devops.scm.sdk.tgit.enums.TGitMergeRequestState;
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
public class TGitMergeRequestFilter {
    private Long iid;
    private List<Long> iids;
    private String sourceBranch;
    private String targetBranch;
    private TGitMergeRequestState state;
    private TGitMergeRequestOrderBy orderBy;
    private TGitSortOrder sort;
    private Date createdAfter;
    private Date createdBefore;
    private Date updatedAfter;
    private Date updatedBefore;
    private Integer page;
    private Integer perPage;
}
