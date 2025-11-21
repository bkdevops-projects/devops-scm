package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeAuthor;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeUser;
import java.util.Date;
import lombok.Data;

@Data
public class BkCodeEventComment {
    @JsonProperty("createdAt")
    private Date createdAt;
    private BkCodeEventUser author;
    private Long id;
    private String body;
    private String url;
    @JsonProperty("updatedAt")
    private Date updatedAt;
}
