package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BkCodeAuthor extends BkCodeBaseUser {
    /**
     * 作者提交时间
     */
    @JsonProperty("authoredDate")
    private Date authoredDate;

    /**
     * 作者名称
     */
    private String name;
}
