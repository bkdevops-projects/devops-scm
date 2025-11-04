package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BkCodeCommitter extends BkCodeBaseUser {
    /**
     * 提交时间
     */
    @JsonProperty("committedDate")
    private Date committedDate;
    /**
     * 提交者名称
     */
    private String name;
}
