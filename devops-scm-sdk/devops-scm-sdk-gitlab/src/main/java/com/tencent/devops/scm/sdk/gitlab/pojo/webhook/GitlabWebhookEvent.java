package com.tencent.devops.scm.sdk.gitlab.pojo.webhook;

import lombok.Data;

@Data
public class GitlabWebhookEvent {
    private String objectKind;
    private String eventType;
}
