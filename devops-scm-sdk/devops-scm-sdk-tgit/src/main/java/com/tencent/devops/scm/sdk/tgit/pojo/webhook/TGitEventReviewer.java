package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.tencent.devops.scm.sdk.tgit.pojo.TGitUser;
import java.util.Date;
import lombok.Data;

@Data
public class TGitEventReviewer {
    private TGitUser reviewer;
    private Long id;
    private Long reviewId;
    private Long userId;
    private Long projectId;
    private String type;
    private String state;
    private Date createdAt;
    private Date updatedAt;
}
