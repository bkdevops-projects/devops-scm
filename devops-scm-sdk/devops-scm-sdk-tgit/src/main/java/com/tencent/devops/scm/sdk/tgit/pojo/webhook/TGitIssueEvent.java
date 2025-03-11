package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.tencent.devops.scm.sdk.tgit.pojo.TGitUser;
import lombok.Data;

@Data
public class TGitIssueEvent {

    public static final String X_GITLAB_EVENT = "Issue Hook";
    public static final String OBJECT_KIND = "issue";

    private String objectKind;
    private TGitUser user;
    private TGitEventRepository repository;
    private ObjectAttributes objectAttributes;

    public static class ObjectAttributes extends TGitEventIssue {
    }
}
