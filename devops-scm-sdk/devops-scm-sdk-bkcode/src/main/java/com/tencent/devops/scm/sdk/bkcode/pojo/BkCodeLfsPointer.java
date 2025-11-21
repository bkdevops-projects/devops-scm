package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BkCodeLfsPointer {
    /**
     * lfs文件大小
     */
    @JsonProperty("size")
    private Long size;
    /**
     * lfs文件oid
     */
    @JsonProperty("oid")
    private String oid;
}