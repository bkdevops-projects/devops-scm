package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GiteeFileChange {
    private String patch;
    private String filename;
    private Long additions;
    private Long deletions;
    private Long changes;
    private Boolean truncated;
    @JsonProperty("content_url")
    private String contentUrl;
    private String sha;
    @JsonProperty("blob_url")
    private String blobUrl;
    @JsonProperty("raw_url")
    private String rawUrl;
    private String status;
}
