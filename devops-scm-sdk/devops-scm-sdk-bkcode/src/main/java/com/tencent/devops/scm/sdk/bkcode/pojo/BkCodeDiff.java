package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class BkCodeDiff {
    /**
     * 新增行数
     */
    @JsonProperty("addLines")
    private Integer addLines;
    /**
     * 删除行数
     */
    @JsonProperty("deleteLines")
    private Integer deleteLines;
    /**
     * 差异文件数量
     */
    @JsonProperty("totalFileCount")
    private Integer totalFileCount;
    /**
     * 已对比文件的文件（不包含超过上限被忽略的部分）
     */
    @JsonProperty("diffFiles")
    private List<BkCodeDiffFile> diffFiles;
}
