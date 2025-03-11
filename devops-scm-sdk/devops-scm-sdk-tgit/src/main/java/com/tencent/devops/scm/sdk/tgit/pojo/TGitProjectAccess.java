package com.tencent.devops.scm.sdk.tgit.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.tgit.enums.TGitAccessLevel;
import lombok.Data;

@Data
public class TGitProjectAccess {
    @JsonProperty("access_level")
    private TGitAccessLevel accessLevel;
}
