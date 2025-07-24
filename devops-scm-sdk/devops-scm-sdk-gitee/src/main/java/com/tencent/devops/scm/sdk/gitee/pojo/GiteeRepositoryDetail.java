
package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GiteeRepositoryDetail extends GiteeBaseRepository {

    @JsonProperty("namespace")
    private GiteeRepositoryNameSpace nameSpace;

    @JsonProperty("default_branch")
    private String defaultBranch;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    private Date updatedAt;
}
