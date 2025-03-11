package com.tencent.devops.scm.sdk.tgit.pojo;

import com.tencent.devops.scm.sdk.tgit.enums.TGitRestrictType;
import com.tencent.devops.scm.sdk.tgit.enums.TGitReviewState;
import com.tencent.devops.scm.sdk.tgit.enums.TGitReviewableType;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class TGitReview {

    private Long id;
    private Long iid;
    private String title;
    private Long projectId;
    private TGitAuthor author;
    private List<TGitReviewer> reviewers;
    private Long reviewableId;
    private TGitReviewableType reviewableType;
    private String commitId;
    private TGitReviewState state;
    private TGitRestrictType restrictType;
    private Boolean pushResetEnabled;
    private Date createdAt;
    private Date updatedAt;
}
