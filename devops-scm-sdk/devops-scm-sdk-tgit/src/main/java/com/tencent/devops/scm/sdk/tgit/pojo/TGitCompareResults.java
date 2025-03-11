package com.tencent.devops.scm.sdk.tgit.pojo;

import java.util.List;
import lombok.Data;

@Data
public class TGitCompareResults {

    private TGitCommit commit;
    private List<TGitCommit> commits;
    private List<TGitDiff> diffs;
    private Boolean compareTimeout;
    private Boolean compareSameRef;
}
