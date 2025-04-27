package com.tencent.devops.scm.sdk.tsvn.pojo;

import lombok.Data;

@Data
public class TSvnEventFile {
    private String file;
    private Boolean isFile;
    private Long size;
    private Long insertions;
    private Long deletions;
    private String type;
    private Boolean directory;
}