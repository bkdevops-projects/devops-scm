package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.tencent.devops.scm.sdk.tgit.pojo.TGitUser;
import lombok.Data;

@Data
public class TGitMergeRequestEvent {
    private String objectKind;
    private Boolean manualUnlock;
    private TGitUser user;
    private ObjectAttributes objectAttributes;

    public static class ObjectAttributes extends TGitEventMergeRequest {
    }
}
