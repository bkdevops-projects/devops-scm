package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
public class BkCodeReviewApproval {

    /**
     * 审批状态：APPROVED、REJECTED 等
     */
    @JsonProperty("approvalStatus")
    private String approvalStatus;
    /**
     * 审批意见
     */
    @JsonProperty("approvalComment")
    private String approvalComment;
    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    private Date createTime;
    /**
     * 审批类型：RULE、MANUAL 等
     */
    @JsonProperty("approvalType")
    private String approvalType;
    /**
     * 关联的审批规则 ID
     */
    @JsonProperty("approvalRuleId")
    private Long approvalRuleId;
    /**
     * 合并请求 ID
     */
    @JsonProperty("mergeRequestId")
    private Long mergeRequestId;
    /**
     * 审批 ID
     */
    private Long id;
    /**
     * 审批人 ID
     */
    @JsonProperty("userId")
    private String userId;
    /**
     * 审批人信息
     */
    private BkCodeUser user;
}