package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class BkCodeCommitDetail {
    /**
     * 提交信息
     */
    @JsonProperty("commitInfo")
    private BkCodeCommit commitInfo;
    /**
     * 所属分支列表
     */
    private List<String> branches;
    /**
     * 关联标签列表
     */
    private List<String> tags;
}
