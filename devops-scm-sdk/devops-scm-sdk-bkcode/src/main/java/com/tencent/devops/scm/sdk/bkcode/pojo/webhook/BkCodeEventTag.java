package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import java.util.Date;
import lombok.Data;

@Data
public class BkCodeEventTag {
    private Date createdAt;
    private BkCodeEventUser tagger;
    private String name;
    private String message;
    private String target;
}