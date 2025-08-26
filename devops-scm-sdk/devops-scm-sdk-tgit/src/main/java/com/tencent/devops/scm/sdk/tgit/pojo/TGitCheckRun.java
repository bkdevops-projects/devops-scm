package com.tencent.devops.scm.sdk.tgit.pojo;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TGitCheckRun {
    private Integer id;
    private String state;
    private String targetUrl;
    private String description;
    private String context;
    private Date createdAt;
    private Date updatedAt;
    private Boolean block;
    // 检测详情，支持 markdown 格式，最大字符串长度：65535
    private String detail;
    // 检查结果关联的 MR（按目标分支识别），
    // target_branches 为空时（默认），检查展示在所有 MR 中，如果传入的目标分支等于~NONE，检查结果不展示在任一 MR 中
    private List<String> targetBranches;

    public static final String ALL_BRANCH_FLAG = "~NONE";
}
