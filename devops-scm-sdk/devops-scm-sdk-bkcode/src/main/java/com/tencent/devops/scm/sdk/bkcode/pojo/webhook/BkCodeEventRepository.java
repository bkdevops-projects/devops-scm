package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BkCodeEventRepository {
    @JsonProperty("private")
    private Boolean repositoryPrivate;
    @JsonProperty("sshUrl")
    private String sshUrl;
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("default")
    private String defaultBranch;
    @JsonProperty("fullName")
    private String fullName;
    private String description;
    private String path;
    @JsonProperty("repoType")
    private String repoType;
    private Boolean fork;
    @JsonProperty("httpUrl")
    private String httpUrl;
    private String name;
    private Long id;
    private String homepage;
    private BkCodeEventGroup group;
}