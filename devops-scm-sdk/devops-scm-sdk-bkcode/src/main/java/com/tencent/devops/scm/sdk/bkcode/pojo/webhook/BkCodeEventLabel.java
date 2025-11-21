package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import lombok.Data;

@Data
public class BkCodeEventLabel {
    private String color;
    private String name;
    private String description;
    private Long id;
}
