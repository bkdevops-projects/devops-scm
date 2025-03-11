package com.tencent.devops.scm.sdk.tgit.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 项目权限
 */
@Data
public class TGitPermissions {
    @JsonProperty("project_access")
    private TGitProjectAccess projectAccess;
    @JsonProperty("share_group_access")
    private TGitProjectAccess shareGroupAccess;
    @JsonProperty("group_access")
    private TGitProjectAccess groupAccess;
}
