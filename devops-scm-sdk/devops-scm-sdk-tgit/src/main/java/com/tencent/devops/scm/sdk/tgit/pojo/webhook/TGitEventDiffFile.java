package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * tgit event变更文件
 */
@Data
public class TGitEventDiffFile {
    @JsonProperty("a_mode")
    private String aMode;
    @JsonProperty("b_mode")
    private String bMode;
    private String oldPath;
    private String newPath;
    private Boolean newFile;
    private Boolean renamedFile;
    private Boolean deletedFile;
}
