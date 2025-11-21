package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
public class BkCodeTag {
    /**
     * 创建人（平台用户）
     */
    private BkCodeUser creator;
    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    private Date createTime;
    /**
     * 标签创建者信息（Git 作者信息）
     */
    private BkCodeAuthor author;
    /**
     * 标签名称
     */
    private String name;
    /**
     * 关联的提交信息
     */
    private BkCodeCommit commit;
    /**
     * 附注标签 message（annotated tag）
     */
    private String message;
    /**
     * 标签描述
     */
    private String desc;
}
