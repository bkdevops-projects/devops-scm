package com.tencent.devops.scm.sdk.gitlab.pojo;

import lombok.Data;

@Data
public class GitlabDiff {
    private String oldPath;
    private String newPath;
    private String aMode;
    private String bMode;
    private String diff;
    private boolean newFile;
    private boolean renamedFile;
    private boolean deletedFile;
    private boolean generatedFile;
    private boolean collapsed;
    private boolean tooLarge;
}
