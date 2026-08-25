package com.tencent.devops.scm.sdk.gitlab.pojo;

import lombok.Data;

@Data
public class GitlabNamespace {
    private Long id;
    private String name;
    private String path;
    private String kind;
    private String fullPath;
    private String webUrl;
}
