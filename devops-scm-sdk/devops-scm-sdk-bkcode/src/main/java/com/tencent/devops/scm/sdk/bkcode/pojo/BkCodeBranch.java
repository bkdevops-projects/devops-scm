package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
public class BkCodeBranch {
    private String name;
    @JsonProperty("protected")
    private boolean protectedBranch;
    @JsonProperty("default")
    private boolean defaultBranch;
    private String description;
    private BkCodeCommit commit;
    private BkCodeUser creator;
    @JsonProperty("createTime")
    private Date createTime;
    @JsonProperty("updateTime")
    private Date updateTime;
}
