package com.tencent.devops.scm.sdk.gitlab.pojo;

import lombok.Data;

@Data
public class GitlabRepositoryFile {
    private String fileName;
    private String filePath;
    private Long size;
    private String encoding;
    private String content;
    private String contentSha256;
    private String ref;
    private String blobId;
    private String commitId;
    private String lastCommitId;
}
