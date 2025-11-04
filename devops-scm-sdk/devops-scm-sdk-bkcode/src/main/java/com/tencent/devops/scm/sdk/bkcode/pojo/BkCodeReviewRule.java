package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class BkCodeReviewRule {
    /**
     * 已通过数量
     */
    @JsonProperty("approvalPassed")
    private Long approvalPassed;
    /**
     * 所需审批数量
     */
    @JsonProperty("approvalRequired")
    private Long approvalRequired;
    /**
     * 审批记录列表
     */
    @JsonProperty("approvalList")
    private List<BkCodeReviewApproval> approvalList;
    /**
     * 规则名称
     */
    @JsonProperty("ruleName")
    private String ruleName;
    /**
     * 规则 ID
     */
    @JsonProperty("ruleId")
    private Long ruleId;
}
