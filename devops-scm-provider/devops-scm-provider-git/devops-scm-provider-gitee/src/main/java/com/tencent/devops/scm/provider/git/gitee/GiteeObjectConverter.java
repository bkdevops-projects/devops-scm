package com.tencent.devops.scm.provider.git.gitee;

import com.tencent.devops.scm.api.constant.DateFormatConstants;
import com.tencent.devops.scm.api.enums.EventAction;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Change.ChangeBuilder;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.Hook;
import com.tencent.devops.scm.api.pojo.HookEvents;
import com.tencent.devops.scm.api.pojo.HookEvents.HookEventsBuilder;
import com.tencent.devops.scm.api.pojo.Milestone;
import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.api.pojo.Signature;
import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.provider.git.gitee.enums.GiteeActionDesc;
import com.tencent.devops.scm.sdk.common.util.DateUtils;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseUser;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCommitCompare;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequestDiff;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeRepositoryDetail;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeWebhookConfig;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventAuthor;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventCommit;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventMilestone;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventRef;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GiteeObjectConverter {

    /*========================================ref====================================================*/
    public static Reference convertBranches(GiteeBranch branch) {
        return Reference.builder()
                .name(branch.getName())
                .sha(branch.getCommit().getSha())
                .linkUrl(branch.getProtectionUrl())
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

    public static GitScmServerRepository convertRepository(GiteeRepositoryDetail repository) {
        return GitScmServerRepository.builder()
                .id(repository.getId())
                .group(repository.getNameSpace().getPath())
                .name(repository.getName())
                .defaultBranch(repository.getDefaultBranch())
                .isPrivate(repository.getPrivateRepository())
                .httpUrl(repository.getHtmlUrl())
                .sshUrl(repository.getSshUrl())
                .webUrl(repository.getHtmlUrl())
                .created(repository.getCreatedAt())
                .updated(repository.getUpdatedAt())
                .fullName(repository.getFullName())
                .build();
    }

    /*========================================pull_request====================================================*/
    public static EventAction convertAction(String sourceAction, String actionDesc) {
        EventAction action = null;
        if (EventAction.UPDATE.value.equals(sourceAction)
                && GiteeActionDesc.SOURCE_BRANCH_CHANGED.toValue().equals(actionDesc)) {
            action = EventAction.PUSH_UPDATE;
        } else {
            action = Arrays.stream(EventAction.values())
                    .filter(item -> item.value.equals(sourceAction))
                    .findAny()
                    .get();
        }
        return action;
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

    /*========================================commit====================================================*/
    public static Commit convertCommit(GiteeEventCommit commit) {
        return Commit.builder()
                .sha(commit.getId())
                .message(commit.getMessage())
                .author(convertSignature(commit.getAuthor()))
                .committer(convertSignature(commit.getCommitter()))
                .link(commit.getUrl())
                .added(commit.getAdded())
                .modified(commit.getModified())
                .removed(commit.getRemoved())
                .commitTime(DateUtils.convertDateToLocalDateTime(commit.getTimestamp()))
                .build();
    }

    /*========================================user====================================================*/
    public static Signature convertSignature(GiteeBaseUser user) {
        return Signature.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User convertUser(GiteeBaseUser user) {
        return User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    /*========================================compare====================================================*/
    public static List<Change> convertCompare(GiteeCommitCompare commitCompare) {
        return commitCompare.getFiles().stream().map(
                file -> {
                    ChangeBuilder changeBuilder = Change.builder()
                            .sha(file.getSha())
                            .path(file.getFilename())
                            .blobId(file.getBlobUrl())
                            .renamed(false);// gitee 的webhook中暂时没办法区分出是rename操作
                    boolean removed = "removed".equals(file.getStatus());
                    boolean added = "added".equals(file.getStatus());
                    return changeBuilder
                            .added(added)
                            .deleted(removed)
                            .build();
                }
        ).collect(Collectors.toList());
    }

    /*========================================hook====================================================*/
    public static Hook convertHook(GiteeWebhookConfig webhookConfig) {
        return Hook.builder()
                .id(webhookConfig.getId())
                .url(webhookConfig.getUrl())
                .events(convertEvents(webhookConfig))
                .active(true)
                .build();
    }

    private static HookEvents convertEvents(GiteeWebhookConfig from) {
        HookEventsBuilder builder = HookEvents.builder();
        if (from.getPushEvents()) {
            builder.push(true);
        }
        if (from.getTagPushEvents()) {
            builder.tag(true);
        }
        if (from.getMergeRequestsEvents()) {
            builder.pullRequest(true);
        }
        if (from.getIssuesEvents()) {
            builder.issue(true);
        }
        if (from.getNoteEvents()) {
            builder.issueComment(true);
            builder.pullRequestComment(true);
        }
        return builder.build();
    }
}
