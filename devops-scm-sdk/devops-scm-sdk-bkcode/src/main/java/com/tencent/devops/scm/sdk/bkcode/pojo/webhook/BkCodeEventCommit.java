package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class BkCodeEventCommit {
    private BkCodeEventUser committer;
    private List<String> removed;
    private List<String> added;
    private BkCodeEventUser author;
    private List<String> modified;
    private String id;
    private String message;
    private String url;
    private Date timestamp;
}