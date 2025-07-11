package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GiteeRepositoryNameSpace {
    private String path;
    @JsonProperty("html_url")
    private String htmlUrl;
    private String name;
    private Long id;
    private String type;
}
