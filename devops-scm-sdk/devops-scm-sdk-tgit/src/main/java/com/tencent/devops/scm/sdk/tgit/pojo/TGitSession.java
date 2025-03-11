package com.tencent.devops.scm.sdk.tgit.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 会话
 */
@Data
public class TGitSession {
    @JsonProperty("private_token")
    private String privateToken;
}
