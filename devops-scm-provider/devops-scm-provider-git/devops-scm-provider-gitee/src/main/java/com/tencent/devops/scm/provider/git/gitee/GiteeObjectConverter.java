package com.tencent.devops.scm.provider.git.gitee;

import com.tencent.devops.scm.api.constant.DateFormatConstants;
import com.tencent.devops.scm.api.enums.EventAction;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Milestone;
import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequestDiff;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventAuthor;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventMilestone;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventRef;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class GiteeObjectConverter {

    /*========================================ref====================================================*/
    public static Reference convertBranches(GiteeBranch branch) {
        return Reference.builder()
                .name(branch.getName())
                .sha(branch.getCommit().getSha())
                .build();
    }

    public static Reference convertRef(GiteeEventRef ref) {
        return Reference.builder()
                .name(ref.getRef())
                .sha(ref.getSha())
                .build();
    }

    /*========================================repository====================================================*/
    public static GitScmServerRepository convertRepository(GiteeEventRepository repository) {
        return GitScmServerRepository.builder()
                .id(repository.getId())
                .group(repository.getNamespace())
                .name(repository.getName())
                .defaultBranch(repository.getDefaultBranch())
                .isPrivate(repository.getPrivateRepository())
                .httpUrl(repository.getGitHttpUrl())
                .sshUrl(repository.getSshUrl())
                .webUrl(repository.getHtmlUrl())
                .created(repository.getCreatedAt())
                .updated(repository.getUpdatedAt())
                .fullName(repository.getFullName())
                .build();
    }

    /*========================================pull_request====================================================*/
    public static EventAction convertAction(String action) {
        return Arrays.stream(EventAction.values())
                .filter(item -> item.value.equals(action))
                .findAny()
                .get();
    }

    /*========================================user====================================================*/
    public static User convertAuthor(GiteeEventAuthor author) {
        return User.builder()
                .id(author.getId())
                .username(author.getUsername())
                .email(author.getEmail())
                .name(author.getName())
                .avatar(author.getAvatarUrl())
                .build();
    }

    /*========================================milestone====================================================*/
    public static Milestone convertMilestone(GiteeEventMilestone milestone) {
        if (milestone == null) {
            return null;
        }
        Date dueDate;
        try {
            dueDate = new SimpleDateFormat(DateFormatConstants.YYYY_MM_DD).parse(milestone.getDueOn());
        } catch (ParseException e) {
            dueDate = null;
        }
        return Milestone.builder()
                .id(milestone.getId().intValue())
                .title(milestone.getTitle())
                .description(milestone.getDescription())
                .state(milestone.getState())
                .iid(milestone.getNumber().intValue())
                .dueDate(dueDate)
                .build();
    }

    public static Change convertChange(GiteePullRequestDiff diff) {
        return Change.builder()
                .path(diff.getFilename())
                .added(diff.getPatch().getNewFile())
                .renamed(diff.getPatch().getRenamedFile())
                .deleted(diff.getPatch().getDeletedFile())
                .sha(diff.getSha())
                .blobId(diff.getSha())
                .oldPath(diff.getPatch().getOldPath())
                .build();
    }
}
