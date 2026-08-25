package com.tencent.devops.scm.sdk.gitlab.pojo;

import lombok.Data;

@Data
public class GitlabTreeItem {
    private String id;
    private String name;
    private String type;
    private String path;
    private String mode;
}
