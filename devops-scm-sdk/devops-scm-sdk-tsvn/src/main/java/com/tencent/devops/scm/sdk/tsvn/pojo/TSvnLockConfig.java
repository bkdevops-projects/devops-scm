package com.tencent.devops.scm.sdk.tsvn.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TSvnLockConfig {
    private String path;
    private String id;
    private String message;
    private TSvnBaseUser user;
    private Long timestamp;
    @JsonProperty("scrollId")
    private String scrollId;
}
