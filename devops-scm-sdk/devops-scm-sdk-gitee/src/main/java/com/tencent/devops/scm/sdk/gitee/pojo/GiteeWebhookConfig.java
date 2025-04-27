package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
public class GiteeWebhookConfig {
    @JsonProperty("push_events")
    private Boolean pushEvents;
    @JsonProperty("tag_push_events")
    private Boolean tagPushEvents;
    private String password;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("issues_events")
    private Boolean issuesEvents;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("merge_requests_events")
    private Boolean mergeRequestsEvents;
    private Long id;
    @JsonProperty("note_events")
    private Boolean noteEvents;
    private String url;
}
