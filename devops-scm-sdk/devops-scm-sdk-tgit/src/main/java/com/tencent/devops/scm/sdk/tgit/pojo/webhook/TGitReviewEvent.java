package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.tencent.devops.scm.sdk.tgit.pojo.TGitUser;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class TGitReviewEvent {

    private String event;
    private TGitUser author;
    private TGitEventReviewer reviewer;
    private List<TGitEventReviewer> reviewers;
    private Long id;
    private Long projectId;
    private TGitEventRepository repository;
    private Integer iid;
    private Long authorId;
    private Long reviewableId;
    private String reviewableType;
    private String commitId;
    private String state;
    private Integer approverRule;
    private Integer necessaryApproverRule;
    private Boolean pushResetEnabled;
    private Integer pushResetRule;
    private Date createdAt;
    private Date updatedAt;
}
