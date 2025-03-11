package com.tencent.devops.scm.sdk.tgit.pojo;

import com.tencent.devops.scm.sdk.tgit.enums.TGitBlobType;
import lombok.Data;

@Data
public class TGitTreeItem {

    // blob_id
    private String id;
    private String mode;
    private String name;
    private TGitBlobType type;
}
