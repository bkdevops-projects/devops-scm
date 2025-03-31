package com.tencent.devops.scm.provider.git.gitee;

import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch;

public class GiteeObjectConverter {

    /*========================================ref====================================================*/
    public static Reference convertBranches(GiteeBranch branch) {
        return Reference.builder()
                .name(branch.getName())
                .sha(branch.getCommit().getSha())
                .build();
    }
}
