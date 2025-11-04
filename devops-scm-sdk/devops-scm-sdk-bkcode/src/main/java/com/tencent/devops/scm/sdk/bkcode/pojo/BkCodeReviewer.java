package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
public class BkCodeReviewer {
    /**
     * 是否必须评审
     */
    @JsonProperty("isRequired")
    private Boolean isRequired;
    /**
     * 评审意见
     */
    @JsonProperty("reviewComment")
    private String reviewComment;
    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    private Date createTime;
    /**
     * 评审时间
     */
    @JsonProperty("reviewedAt")
    private Date reviewedAt;
    /**
     * 合并请求 ID
     */
    @JsonProperty("mergeRequestId")
    private Long mergeRequestId;
    /**
     * 评审人 ID
     */
    private Long id;
    /**
     * 评审人信息
     */
    private BkCodeUser reviewer;
    /**
     * 评审状态：PENDING、APPROVED、REJECTED 等
     */
    private String status;
}
