package com.tencent.devops.scm.sdk.tsvn.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class TSvnPostCommitEvent {
    @JsonProperty("user_email")
    private String userEmail;
    @JsonProperty("rep_name")
    private String repName;
    private String log;
    private String tSvnPostCommitHookUserName;
    @JsonProperty("total_files_count")
    private Long totalFilesCount;
    private TSvnEventRepository repository;
    private String message;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("object_kind")
    private String objectKind;
    private Long revision;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("project_id")
    private Long projectId;
    private Long commitTime;
    private List<String> paths;
    private List<TSvnEventFile> files;
    @JsonProperty("push_timestamp")
    private String pushTimestamp;
    @JsonProperty("action_kind")
    private String actionKind;
    @JsonProperty("operation_kind")
    private String operationKind;
    private String timestamp;
}
