package com.tencent.devops.scm.sdk.tsvn.pojo;

import lombok.Data;

@Data
public class TSvnCommit {
    private String author;
    private String id;
    private String message;
    private String when;
}
