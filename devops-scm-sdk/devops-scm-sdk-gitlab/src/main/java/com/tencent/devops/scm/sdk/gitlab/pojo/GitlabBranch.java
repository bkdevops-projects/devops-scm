package com.tencent.devops.scm.sdk.gitlab.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitlabBranch {
    private String name;
    private boolean merged;
    @JsonProperty("protected")
    private boolean protectedBranch;
    @JsonProperty("default")
    private boolean defaultBranch;
    private boolean canPush;
    private String webUrl;
    private GitlabCommit commit;
}
