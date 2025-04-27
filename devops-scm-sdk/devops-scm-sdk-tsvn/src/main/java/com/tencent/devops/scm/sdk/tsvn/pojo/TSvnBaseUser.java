package com.tencent.devops.scm.sdk.tsvn.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TSvnBaseUser {
    @JsonProperty("web_url")
    private String webUrl;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    private String name;
    private Long id;
    private String state;
    private String username;
}