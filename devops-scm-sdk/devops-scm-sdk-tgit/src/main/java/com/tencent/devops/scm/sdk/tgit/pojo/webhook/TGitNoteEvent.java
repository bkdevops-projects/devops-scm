package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.tencent.devops.scm.sdk.tgit.pojo.TGitReviewAttributes;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitUser;
import lombok.Data;

@Data
public class TGitNoteEvent {
    public static final String X_GITLAB_EVENT = "Note Hook";
    public static final String OBJECT_KIND = "note";

    private TGitUser user;
    private Long projectId;
    private TGitEventRepository repository;
    private ObjectAttributes objectAttributes;
    private TGitEventCommit commit;
    private TGitEventIssue issue;
    private TGitEventMergeRequest mergeRequest;
    private TGitReviewAttributes review;

    public static class ObjectAttributes extends TGitEventNote {
    }
}
