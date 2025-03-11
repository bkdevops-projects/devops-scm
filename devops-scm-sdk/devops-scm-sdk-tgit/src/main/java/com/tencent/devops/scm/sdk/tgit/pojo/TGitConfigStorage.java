package com.tencent.devops.scm.sdk.tgit.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TGitConfigStorage {
    @JsonProperty("limit_lfs_file_size")
    private Integer limitLfsFileSize;
    @JsonProperty("limit_size")
    private Integer limitSize;
    @JsonProperty("limit_file_size")
    private Float limitFileSize;
    @JsonProperty("limit_lfs_size")
    private Float limitLfsSize;
}
