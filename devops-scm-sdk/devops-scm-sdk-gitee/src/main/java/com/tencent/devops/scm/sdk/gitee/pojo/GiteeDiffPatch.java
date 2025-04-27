package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GiteeDiffPatch {
    @JsonProperty("new_path")
    private String newPath;
    @JsonProperty("renamed_file")
    private Boolean renamedFile;
    @JsonProperty("too_large")
    private Boolean tooLarge;
    @JsonProperty("a_mode")
    private String aMode;
    @JsonProperty("deleted_file")
    private Boolean deletedFile;
    @JsonProperty("b_mode")
    private String bMode;
    @JsonProperty("new_file")
    private Boolean newFile;
    private String diff;
    @JsonProperty("old_path")
    private String oldPath;
}