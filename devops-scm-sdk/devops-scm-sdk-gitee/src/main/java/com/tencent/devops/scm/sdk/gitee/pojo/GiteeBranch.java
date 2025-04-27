package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class GiteeBranch {
    private String name;
    @JsonProperty("commit")
    private GiteeBranchCommit commit;
    @JsonProperty("protected")
    private Boolean protectedBranch;
    @JsonProperty("protection_url")
    private String protectionUrl;
}


