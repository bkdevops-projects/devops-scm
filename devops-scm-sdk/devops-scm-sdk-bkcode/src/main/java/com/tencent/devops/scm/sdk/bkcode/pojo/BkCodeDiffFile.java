package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeDiffFileChangeType;
import lombok.Data;

@Data
public class BkCodeDiffFile {
    /**
     * 是否存在冲突
     */
    @JsonProperty("conflicted")
    private Boolean conflicted;
    /**
     * 文件名
     */
    @JsonProperty("fileName")
    private String fileName;
    /**
     * 原始文件 mode
     */
    @JsonProperty("srcFileMode")
    private String srcFileMode;
    /**
     * 原始文件是否是lfs
     */
    @JsonProperty("srcLfs")
    private Boolean srcLfs;
    /**
     * 目标文件lfs指针
     */
    @JsonProperty("dstLfsPointer")
    private BkCodeLfsPointer dstLfsPointer;
    /**
     * 是否比对文件大小超过限制
     */
    @JsonProperty("tooLarge")
    private Boolean tooLarge;
    /**
     * 删除行数
     */
    @JsonProperty("deleteLines")
    private Long deleteLines;
    /**
     * 源文件行数（文本类型）
     */
    @JsonProperty("srcFileLineCount")
    private Long srcFileLineCount;
    /**
     * 文件变更类型（ADD/MODIFY/DELETE/RENAME/COPY）
     */
    @JsonProperty("changeType")
    private String changeType;
    /**
     * 原始文件路径
     */
    @JsonProperty("srcPath")
    private String srcPath;
    /**
     * 目标文件对应的 git object id
     */
    @JsonProperty("dstObjectId")
    private String dstObjectId;
    /**
     * 原始文件大小
     */
    @JsonProperty("srcFileSize")
    private Long srcFileSize;
    /**
     * 目标文件 mode
     */
    @JsonProperty("dstFileMode")
    private String dstFileMode;
    /**
     * 原始文件lfs指针
     */
    @JsonProperty("srcLfsPointer")
    private BkCodeLfsPointer srcLfsPointer;
    /**
     * 目标文件行数（文本类型）
     */
    @JsonProperty("dstFileLineCount")
    private Long dstFileLineCount;
    /**
     * 是否是二进制文件
     */
    @JsonProperty("binary")
    private Boolean binary;
    /**
     * 新增行数
     */
    @JsonProperty("addLines")
    private Long addLines;
    /**
     * 原始文件对应的 git object id
     */
    @JsonProperty("srcObjectId")
    private String srcObjectId;
    /**
     * 目标文件大小
     */
    @JsonProperty("dstFileSize")
    private Long dstFileSize;
    /**
     * 目标文件是否是lfs
     */
    @JsonProperty("dstLfs")
    private Boolean dstLfs;
    /**
     * 目标文件路径
     */
    @JsonProperty("dstPath")
    private String dstPath;

    public Boolean created() {
        return BkCodeDiffFileChangeType.ADD.name().equals(changeType);
    }

    public Boolean removed() {
        return BkCodeDiffFileChangeType.DELETE.name().equals(changeType);
    }

    public Boolean updated() {
        return BkCodeDiffFileChangeType.MODIFY.name().equals(changeType);
    }

    public Boolean renamed() {
        return BkCodeDiffFileChangeType.RENAME.name().equals(changeType);
    }
}
