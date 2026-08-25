package com.tencent.devops.scm.sdk.gitlab.pojo;

import java.util.List;
import lombok.Data;

@Data
public class GitlabCompareResults {
    private GitlabCommit commit;
    private List<GitlabCommit> commits;
    private List<GitlabDiff> diffs;
    private boolean compareTimeout;
    private boolean compareSameRef;
}
