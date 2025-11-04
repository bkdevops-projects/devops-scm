package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
public class BkCodeLabel {
    private Integer id;
    private String name;
    private String color;
    private BkCodeUser creator;
    private BkCodeUser updater;
    @JsonProperty("createTime")
    private Date createTime;
    @JsonProperty("updateTime")
    private Date updateTime;
}
