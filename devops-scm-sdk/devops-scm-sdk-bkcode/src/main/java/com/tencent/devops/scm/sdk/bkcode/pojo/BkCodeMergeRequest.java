package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import java.util.Date;

@Data
public class BkCodeMergeRequest {
    /** 合并请求ID */
    private Long id;
    
    /** 标识，仓库下唯一，从1开始递增 */
    private Integer code;
    
    /** 目标仓库ID */
    @JsonProperty("targetRepoId")
    private Long targetRepoId;
    
    /** 源仓库ID */
    @JsonProperty("sourceRepoId")
    private Long sourceRepoId;

    /** 目标分支 */
    @JsonProperty("targetBranch")
    private String targetBranch;

    /** 源分支 */
    @JsonProperty("sourceBranch")
    private String sourceBranch;
    
    /** 合并基准 Commit ID */
    @JsonProperty("baseCommitId")
    private String baseCommitId;
    
    /** 源分支最新 Commit ID */
    @JsonProperty("headCommitId")
    private String headCommitId;
    
    /** 标题 */
    private String title;
    
    /** 描述 */
    private String description;
    
    /** 状态：OPEN、MERGED、CLOSED */
    private String status;
    
    /** 合并策略：NO_FAST_FORWARD、FAST_FORWARD、SQUASH */
    @JsonProperty("mergeStrategy")
    private String mergeStrategy;
    
    /** 是否存在冲突 */
    @JsonProperty("isConflicted")
    private Boolean isConflicted;
    
    /** 分支是否被删除 */
    @JsonProperty("isBranchDeleted")
    private Boolean isBranchDeleted;
    
    /** 合并执行人 */
    @JsonProperty("mergedBy")
    private BkCodeUser mergedBy;
    
    /** 合并时间 */
    @JsonProperty("mergedAt")
    private Date mergedAt;
    
    /** 关闭人 */
    @JsonProperty("closedBy")
    private BkCodeUser closedBy;
    
    /** 关闭时间 */
    @JsonProperty("closedAt")
    private Date closedAt;
    
    /** 创建人 */
    private BkCodeUser creator;
    
    /** 创建时间 */
    @JsonProperty("createTime")
    private Date createTime;
    
    /** 更新人 */
    private BkCodeUser updater;
    
    /** 更新时间 */
    @JsonProperty("updateTime")
    private Date updateTime;
    
    /** 标签列表 */
    private List<BkCodeLabel> labels;
    
    /** 关注者列表 */
    private List<BkCodeUser> followers;
    
    /** 评审人列表 */
    private List<BkCodeReviewer> reviewers;
    
    /** 规则审批记录列表 */
    @JsonProperty("ruleApproval")
    private List<BkCodeReviewRule> ruleApproval;
    
    /** 是否不满足评审条件 */
    @JsonProperty("notSatisfiedReview")
    private Boolean notSatisfiedReview;
    
    /** 合并状态：CAN_BE_MERGED、CANNOT_BE_MERGED 等 */
    @JsonProperty("mergeStatus")
    private String mergeStatus;
    
    /** 最近一次 push 提交 ID */
    @JsonProperty("lastPushCommitId")
    private String lastPushCommitId;
}
