package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BkCodeEventGroup {
    private String path;
    @JsonProperty("groupType")
    private String groupType;
    @JsonProperty("displayName")
    private String displayName;
    private String name;
    private String description;
    private Long id;
}