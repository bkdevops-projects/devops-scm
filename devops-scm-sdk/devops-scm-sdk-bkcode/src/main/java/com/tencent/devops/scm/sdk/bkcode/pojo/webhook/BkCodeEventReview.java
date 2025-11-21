package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import lombok.Data;

@Data
public class BkCodeEventReview {
    private String state;
    private String body;
    private BkCodeEventUser user;
}