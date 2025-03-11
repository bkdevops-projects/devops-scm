package com.tencent.devops.scm.sdk.tgit.pojo;

import lombok.Data;

@Data
public class TGitIdentity {
    private String provider;
    private String externUid;
}
