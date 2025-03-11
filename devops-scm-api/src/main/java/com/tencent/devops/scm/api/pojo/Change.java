package com.tencent.devops.scm.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Change {
    private String path;
    private Boolean added;
    private Boolean renamed;
    private Boolean deleted;
    private String sha;
    private String blobId;
    private String oldPath;
}
