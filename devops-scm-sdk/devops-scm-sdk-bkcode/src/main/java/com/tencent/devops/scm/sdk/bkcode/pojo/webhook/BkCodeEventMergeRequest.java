package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class BkCodeEventMergeRequest {
    @JsonProperty("sourceRepository")
    private BkCodeEventRepository sourceRepository;
    @JsonProperty("requestedReviewers")
    private List<String> requestedReviewers;
    @JsonProperty("sourceBranch")
    private String sourceBranch;
    private BkCodeEventUser author;
    private String description;
    private String title;
    private String url;
    private List<BkCodeEventLabel> labels;
    private Integer number;
    @JsonProperty("createdAt")
    private Date createdAt;
    @JsonProperty("targetBranch")
    private String targetBranch;
    private Long id;
    private String state;
    @JsonProperty("targetRepository")
    private BkCodeEventRepository targetRepository;
    @JsonProperty("mergeStatus")
    private String mergeStatus;
    @JsonProperty("updatedAt")
    private Date updatedAt;
    /**
     * 合并操作人
     */
    private BkCodeEventUser mergeBy;
    /**
     * 合并时间
     */
    private Date mergedAt;
    /**
     * 关闭操作人
     */
    @JsonProperty("closedBy")
    private BkCodeEventUser closedBy;
    /**
     * 关闭
     */
    @JsonProperty("closedAt")
    private Date closedAt;
    /**
     * [action == "edited"]时存在，编辑的内容
     */
    private BkCodeEventMergeRequestChange changes;
    @JsonProperty("mergeCommitSha")
    private String mergeCommitSha;
    @JsonProperty("reviewers")
    private List<BkCodeEventUser> reviewers;
}