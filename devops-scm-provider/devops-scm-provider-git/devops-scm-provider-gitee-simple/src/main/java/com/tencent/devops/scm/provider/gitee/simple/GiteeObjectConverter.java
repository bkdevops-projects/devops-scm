package com.tencent.devops.scm.provider.gitee.simple;

import com.gitee.sdk.gitee5j.model.Branch;
import com.gitee.sdk.gitee5j.model.CompleteBranch;
import com.gitee.sdk.gitee5j.model.Project;
import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import java.util.Date;

public class GiteeObjectConverter {

    /*========================================ref====================================================*/
    public static Reference convertBranches(Branch branch) {
        return Reference.builder()
                .name(branch.getName())
                .sha(branch.getCommit().getSha())
                .linkUrl(branch.getProtectionUrl())
                .build();
    }

    public static Reference convertBranches(CompleteBranch branch) {
        return Reference.builder()
                .name(branch.getName())
                .sha(branch.getCommit().getSha())
                .linkUrl(branch.getProtectionUrl())
                .build();
    }

    /*========================================repository====================================================*/
    public static GitScmServerRepository convertRepository(Project repository) {
        return GitScmServerRepository.builder()
                .id(repository.getId().longValue())
                .group(repository.getNamespace().getPath())
                .name(repository.getName())
                .defaultBranch(repository.getDefaultBranch())
                .isPrivate(repository.isPrivate())
                .httpUrl(repository.getHtmlUrl())
                .sshUrl(repository.getSshUrl())
                .webUrl(repository.getHtmlUrl())
                .created(Date.from(repository.getCreatedAt().toInstant()))
                .updated(Date.from(repository.getUpdatedAt().toInstant()))
                .fullName(repository.getFullName())
                .build();
    }
}
