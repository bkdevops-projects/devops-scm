package com.tencent.devops.scm.sdk.gitlab.pojo;

import java.util.Date;
import lombok.Data;

@Data
public class GitlabProjectHook {
    private Long id;
    private String url;
    private Long projectId;
    private boolean pushEvents;
    private boolean tagPushEvents;
    private boolean issuesEvents;
    private boolean mergeRequestsEvents;
    private boolean noteEvents;
    private boolean enableSslVerification;
    private Date createdAt;
}
