package com.tencent.devops.scm.sdk.gitee.pojo;

import lombok.Data;

@Data
public class GiteePullRequestDiff {
    private GiteeDiffPatch patch;
    private String filename;
    private String additions;
    private String deletions;
    private String sha;
    private String blobUrl;
    private String rawUrl;
}
