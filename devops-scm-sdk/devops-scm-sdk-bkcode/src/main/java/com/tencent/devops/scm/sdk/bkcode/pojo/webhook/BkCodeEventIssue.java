package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeUser;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class BkCodeEventIssue {
    private BkCodeUser author;
    private String description;
    private List<BkCodeEventUser> assignees;
    private String title;
    private String priority;
    private String url;
    private List<BkCodeEventLabel> labels;
    private Long number;
    @JsonProperty("createdAt")
    private Date createdAt;
    private Long id;
    private String state;
    @JsonProperty("updatedAt")
    private Date updatedAt;
}