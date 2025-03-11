package com.tencent.devops.scm.sdk.tgit.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TGitDiff {
    @JsonProperty("a_mode")
    private String aMode;
    @JsonProperty("b_mode")
    private String bMode;
    @JsonProperty("deleted_file")
    private Boolean deletedFile;
    private String diff;
    @JsonProperty("new_file")
    private Boolean newFile;
    @JsonProperty("new_path")
    private String newPath;
    @JsonProperty("old_path")
    private String oldPath;
    @JsonProperty("renamed_file")
    private Boolean renamedFile;
    @JsonProperty("is_too_large")
    private Boolean tooLarge;
    @JsonProperty("is_collapse")
    private Boolean collapse;
    private Integer additions;
    private Integer deletions;
}
