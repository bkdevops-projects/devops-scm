package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.gitee.enums.GiteeCheckRunConclusion;
import com.tencent.devops.scm.sdk.gitee.enums.GiteeCheckRunStatus;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对应JSON结构的Java实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiteeCheckRun {
    @JsonProperty("pull_request_id")
    private Long pullRequestId;
    @JsonProperty("id")
    private Long id;

    @JsonProperty("head_sha")
    private String headSha;

    @JsonProperty("url")
    private String url;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("details_url")
    private String detailsUrl;

    @JsonProperty("status")
    private GiteeCheckRunStatus status;

    @JsonProperty("conclusion")
    private GiteeCheckRunConclusion conclusion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "GMT+8")
    @JsonProperty("started_at")
    private Date startedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "GMT+8")
    @JsonProperty("completed_at")
    private Date completedAt;

    @JsonProperty("output")
    private GiteeCheckRunOutput output;

    @JsonProperty("name")
    private String name;
}