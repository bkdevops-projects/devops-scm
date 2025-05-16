package com.tencent.devops.scm.sdk.tsvn.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 会话
 */
@Data
public class TSvnSession {
    @JsonProperty("private_token")
    private String privateToken;
}
