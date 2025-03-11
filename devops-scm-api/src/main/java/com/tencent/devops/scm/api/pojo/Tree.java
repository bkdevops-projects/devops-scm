package com.tencent.devops.scm.api.pojo;

import com.tencent.devops.scm.api.enums.ContentKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tree {
    private String path;
    private String sha;
    private String blobId;
    private ContentKind kind;
}
