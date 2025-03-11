package com.tencent.devops.scm.sdk.tgit.pojo;

import com.tencent.devops.scm.sdk.tgit.enums.TGitReviewLineType;
import com.tencent.devops.scm.sdk.tgit.enums.TGitReviewState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TGitReviewNoteParams {

    @NonNull
    private String body;
    private String path;
    private Integer line;
    private TGitReviewLineType lineType;
    private TGitReviewState reviewerState;
}
