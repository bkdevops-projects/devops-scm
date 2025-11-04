package com.tencent.devops.scm.sdk.bkcode.pojo;

import lombok.Data;

@Data
public class BkCodeMergeRequestDiff {
    private String aMode;
    private String bMode;
    private Boolean deletedFile;
    private String diff;
    private Boolean newFile;
    private String newPath;
    private String oldPath;
    private Boolean renamedFile;
    private Boolean tooLarge;
    private Boolean collapse;
    private Integer additions;
    private Integer deletions;
}
