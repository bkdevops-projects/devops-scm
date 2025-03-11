package com.tencent.devops.scm.sdk.tgit.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TGitNamespace {
    private Integer id;
    private String name;
    private String path;
    @JsonProperty("owner_id")
    private Integer ownerId;
    private String description;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("updated_at")
    private Date updatedAt;
}
