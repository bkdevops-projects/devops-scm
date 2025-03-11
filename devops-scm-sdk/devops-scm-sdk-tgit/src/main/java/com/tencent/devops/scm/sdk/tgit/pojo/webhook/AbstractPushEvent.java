package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class AbstractPushEvent {
    private String eventName;

    @JsonProperty("object_kind")
    private String objectKind;
    @JsonProperty("operation_kind")
    private String operationKind;
    @JsonProperty("action_kind")
    private String actionKind;

    private String after;
    private String before;
    private String ref;
    @JsonProperty("checkout_sha")
    private String checkoutSha;

    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("project_id")
    private Long projectId;
    private TGitEventRepository repository;
    private List<TGitEventCommit> commits;

    @JsonProperty("push_options")
    private Map<String, String> pushOptions;
    @JsonProperty("push_timestamp")
    private Date pushTimestamp;
    @JsonProperty("total_commits_count")
    private Integer totalCommitsCount;
}
