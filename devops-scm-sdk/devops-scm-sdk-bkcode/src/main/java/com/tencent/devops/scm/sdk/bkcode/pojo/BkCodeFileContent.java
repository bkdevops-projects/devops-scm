package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BkCodeFileContent {
    private String mode;
    private String path;
    private Long size;
    private String name;
    private Boolean lfs;
    @JsonProperty("lfsPointer")
    private BkCodeLfsPointer lfsPointer;
    private Boolean text;
    private String content;
}
