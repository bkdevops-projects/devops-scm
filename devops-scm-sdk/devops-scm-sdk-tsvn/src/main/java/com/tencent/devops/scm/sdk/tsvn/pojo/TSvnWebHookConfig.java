package com.tencent.devops.scm.sdk.tsvn.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TSvnWebHookConfig {
    private String path;
    @JsonProperty("svn_pre_commit_events")
    private Boolean svnPreCommitEvents;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("svn_post_lock_events")
    private Boolean svnPostLockEvents;
    @JsonProperty("created_at")
    private String createdAt;
    private Boolean active;
    @JsonProperty("svn_post_commit_events")
    private Boolean svnPostCommitEvents;
    @JsonProperty("svn_pre_lock_events")
    private Boolean svnPreLockEvents;
    private Long id;
    private String url;
}
