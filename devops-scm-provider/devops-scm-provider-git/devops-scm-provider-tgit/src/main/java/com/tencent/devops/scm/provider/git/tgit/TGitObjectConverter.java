package com.tencent.devops.scm.provider.git.tgit;

import com.tencent.devops.scm.api.enums.ContentKind;
import com.tencent.devops.scm.api.enums.ReviewState;
import com.tencent.devops.scm.api.enums.Visibility;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Comment;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.Content;
import com.tencent.devops.scm.api.pojo.Issue;
import com.tencent.devops.scm.api.pojo.Milestone;
import com.tencent.devops.scm.api.pojo.PullRequest;
import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.api.pojo.Review;
import com.tencent.devops.scm.api.pojo.Signature;
import com.tencent.devops.scm.api.pojo.Tree;
import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.api.pojo.repository.git.GitRepositoryUrl;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.sdk.common.util.UrlConverter;
import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueState;
import com.tencent.devops.scm.sdk.tgit.enums.TGitReviewState;
import com.tencent.devops.scm.sdk.tgit.enums.TGitVisibility;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitAssignee;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitAuthor;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitBranch;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommit;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitDiff;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitIssue;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequest;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMilestone;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitNote;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProject;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitRepositoryFile;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitReview;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitReviewAttributes;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitReviewer;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTag;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTreeItem;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitUser;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventCommit;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventIssue;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventMergeRequest;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventProject;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventRepository;
import com.tencent.devops.scm.sdk.tgit.util.TGitDateUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

public class TGitObjectConverter {

    /*========================================repositories====================================================*/
    public static GitScmServerRepository convertRepository(Long id, TGitEventRepository src) {
        String httpUrl = src.getRealHttpUrl();
        GitRepositoryUrl repositoryUrl = new GitRepositoryUrl(httpUrl);
        return GitScmServerRepository.builder()
                .id(id)
                .group(repositoryUrl.getGroup())
                .name(repositoryUrl.getName())
                .fullName(repositoryUrl.getFullName())
                .httpUrl(httpUrl)
                .sshUrl(StringUtils.defaultIfBlank(src.getGitSshUrl(), UrlConverter.gitHttp2Ssh(httpUrl)))
                .webUrl(src.getHomepage())
                .build();
    }

    public static GitScmServerRepository convertRepository(TGitProject project) {
        GitRepositoryUrl repositoryUrl = new GitRepositoryUrl(project.getHttpUrlToRepo());
        return GitScmServerRepository.builder()
                .id(project.getId())
                .group(repositoryUrl.getGroup())
                .name(project.getName())
                .fullName(repositoryUrl.getFullName())
                .defaultBranch(project.getDefaultBranch())
                .archived(project.getArchived())
                .isPrivate(convertPrivate(project.getVisibilityLevel()))
                .visibility(convertVisibility(project.getVisibilityLevel()))
                .httpUrl(project.getHttpsUrlToRepo())
                .sshUrl(project.getSshUrlToRepo())
                .webUrl(project.getWebUrl())
                .created(project.getCreatedAt())
                .updated(project.getCreatedAt())
                .build();
    }

    private static boolean convertPrivate(int visibilityLevel) {
        return visibilityLevel == TGitVisibility.PRIVATE.toValue();
    }

    private static Visibility convertVisibility(int visibilityLevel) {
        TGitVisibility tgitVisibility = TGitVisibility.forValue(visibilityLevel);
        if (tgitVisibility == null) {
            return Visibility.UNDEFINEd;
        }
        Visibility visibility;
        switch (tgitVisibility) {
            case PRIVATE:
                visibility = Visibility.PRIVATE;
                break;
            case INTERNAL:
                visibility = Visibility.INTERNAL;
                break;
            case PUBLIC:
                visibility = Visibility.PUBLIC;
                break;
            default:
                return Visibility.UNDEFINEd;
        }
        return visibility;
    }

    /*========================================file====================================================*/
    public static Content convertContent(TGitRepositoryFile from) {
        return Content.builder()
                .path(from.getFilePath())
                .content(from.getDecodedContentAsString())
                .sha(from.getCommitId())
                .blobId(from.getBlobId())
                .build();
    }

    public static Tree convertTree(TGitTreeItem from) {
        Tree.TreeBuilder builder = Tree.builder()
                .path(from.getName())
                .blobId(from.getId());
        switch (from.getMode()) {
            case "100644":
            case "100664":
            case "100755":
                builder.kind(ContentKind.FILE);
                break;
            case "040000":
                builder.kind(ContentKind.DIRECTORY);
                break;
            case "120000":
                builder.kind(ContentKind.SYMLINK);
                break;
            case "160000":
                builder.kind(ContentKind.GITLINK);
                break;
            default:
                builder.kind(ContentKind.UNSUPPORTED);
        }
        return builder.build();

    }

    /*========================================pull request====================================================*/
    public static PullRequest convertPullRequest(User author, TGitEventMergeRequest eventMergeRequest) {
        TGitEventProject srcTarget = eventMergeRequest.getTarget();
        TGitEventProject srcSource = eventMergeRequest.getSource();
        GitRepositoryUrl targetRepositoryUrl = new GitRepositoryUrl(srcTarget.getHttpUrl());
        GitScmServerRepository target = GitScmServerRepository.builder()
                .id(eventMergeRequest.getTargetProjectId())
                .group(targetRepositoryUrl.getGroup())
                .name(srcTarget.getName())
                .fullName(targetRepositoryUrl.getFullName())
                .httpUrl(srcTarget.getHttpUrl())
                .sshUrl(srcTarget.getSshUrl())
                .webUrl(srcTarget.getWebUrl())
                .build();

        GitRepositoryUrl sourceRepositoryUrl = new GitRepositoryUrl(srcSource.getHttpUrl());
        GitScmServerRepository source = GitScmServerRepository.builder()
                .id(eventMergeRequest.getSourceProjectId())
                .group(sourceRepositoryUrl.getGroup())
                .name(srcSource.getName())
                .fullName(sourceRepositoryUrl.getFullName())
                .httpUrl(srcSource.getHttpUrl())
                .sshUrl(srcSource.getSshUrl())
                .webUrl(srcSource.getWebUrl())
                .build();

        Reference base = Reference.builder()
                .name(eventMergeRequest.getTargetBranch())
                .build();

        Reference head = Reference.builder()
                .name(eventMergeRequest.getSourceBranch())
                .sha(eventMergeRequest.getLastCommit().getId())
                .build();

        String description = eventMergeRequest.getDescription();

        return PullRequest.builder()
                .id(eventMergeRequest.getId())
                .number(eventMergeRequest.getIid())
                .title(eventMergeRequest.getTitle())
                .body(eventMergeRequest.getDescription())
                .link(eventMergeRequest.getUrl())
                .sha(eventMergeRequest.getLastCommit().getId())
                .targetRepo(target)
                .sourceRepo(source)
                .targetRef(base)
                .sourceRef(head)
                .closed(!"opened".equals(eventMergeRequest.getState()))
                .merged("merged".equals(eventMergeRequest.getState()))
                .mergeType(eventMergeRequest.getMergeType())
                .mergeCommitSha(eventMergeRequest.getMergeCommitSha())
                .author(author)
                .created(eventMergeRequest.getCreatedAt())
                .updated(eventMergeRequest.getUpdatedAt())
                .description(description)
                .build();
    }

    public static PullRequest convertPullRequest(TGitMergeRequest from, TGitProject sourceProject,
            @NonNull TGitProject targetProject) {
        Reference base = Reference.builder()
                .name(from.getTargetBranch())
                .sha(from.getTargetCommit())
                .build();

        Reference head = Reference.builder()
                .name(from.getSourceBranch())
                .sha(from.getSourceCommit())
                .build();

        GitRepositoryUrl targetRepositoryUrl = new GitRepositoryUrl(targetProject.getHttpsUrlToRepo());
        GitScmServerRepository target = GitScmServerRepository.builder()
                .id(from.getSourceProjectId())
                .group(targetRepositoryUrl.getGroup())
                .name(targetRepositoryUrl.getName())
                .fullName(targetRepositoryUrl.getFullName())
                .httpUrl(targetProject.getHttpsUrlToRepo())
                .sshUrl(targetProject.getSshUrlToRepo())
                .webUrl(targetProject.getWebUrl())
                .build();

        GitScmServerRepository source;
        if (from.getTargetProjectId().equals(from.getSourceProjectId())) {
            source = target;
        } else {
            source = Optional.ofNullable(sourceProject).map(proj -> {
                GitRepositoryUrl sourceRepositoryUrl = new GitRepositoryUrl(proj.getHttpsUrlToRepo());
                return GitScmServerRepository.builder()
                        .id(from.getSourceProjectId())
                        .group(sourceRepositoryUrl.getGroup())
                        .name(sourceRepositoryUrl.getName())
                        .fullName(sourceRepositoryUrl.getFullName())
                        .httpUrl(proj.getHttpsUrlToRepo())
                        .sshUrl(proj.getSshUrlToRepo())
                        .webUrl(proj.getWebUrl())
                        .build();
            }).orElseGet(() -> GitScmServerRepository.builder()
                    .id(from.getSourceProjectId())
                    .name("")
                    .group("")
                    .fullName("")
                    .webUrl("")
                    .httpUrl("")
                    .sshUrl("")
                    .build()
            );
        }

        List<User> assignees = Optional.ofNullable(from.getAssignee())
                .map(it -> Collections.singletonList(TGitObjectConverter.convertUser(it)))
                .orElse(Collections.emptyList());

        return PullRequest.builder()
                .id(from.getId())
                .number(from.getIid())
                .title(from.getTitle())
                .body(from.getDescription())
                .link(target.getWebUrl() + "/merge_requests/" + from.getIid())
                .targetRef(base)
                .sourceRef(head)
                .sourceRepo(source)
                .targetRepo(target)
                .closed(!"opened".equals(from.getState()))
                .merged("merged".equals(from.getState()))
                .author(TGitObjectConverter.convertUser(from.getAuthor()))
                .created(from.getCreatedAt())
                .updated(from.getUpdatedAt())
                .milestone(convertMilestone(from.getMilestone()))
                .baseCommit(from.getBaseCommit())
                .labels(from.getLabels())
                .assignee(assignees)
                .build();
    }

    /*========================================ref====================================================*/
    public static Commit convertCommit(TGitEventCommit eventCommit) {
        Signature committer = new Signature();
        committer.setName(eventCommit.getAuthor().getName());
        committer.setEmail(eventCommit.getAuthor().getEmail());

        return Commit.builder()
                .sha(eventCommit.getId())
                .message(eventCommit.getMessage())
                .commitTime(TGitDateUtils.convertDateToLocalDateTime(eventCommit.getTimestamp()))
                .link(eventCommit.getUrl())
                .author(committer)
                .committer(committer)
                .added(eventCommit.getAdded())
                .modified(eventCommit.getModified())
                .removed(eventCommit.getRemoved())
                .build();
    }

    public static Commit convertCommit(TGitCommit from) {
        Signature author = Signature.builder()
                .name(from.getAuthorName())
                .email(from.getAuthorEmail())
                .build();
        Signature committer = Signature.builder()
                .name(from.getCommitterName())
                .email(from.getCommitterEmail())
                .build();
        return Commit.builder()
                .sha(from.getId())
                .message(from.getMessage())
                .author(author)
                .committer(committer)
                .commitTime(TGitDateUtils.convertDateToLocalDateTime(from.getCommittedDate()))
                .build();
    }

    public static Reference convertBranch(TGitBranch from) {
        return Reference.builder()
                .name(from.getName())
                .sha(from.getCommit().getId())
                .build();
    }

    public static Reference convertTag(TGitTag from) {
        return Reference.builder()
                .name(from.getName())
                .sha(from.getCommit().getId())
                .build();
    }

    public static Change convertChange(TGitDiff src) {
        return Change.builder()
                .added(src.getNewFile())
                .renamed(src.getRenamedFile())
                .deleted(src.getDeletedFile())
                .path(src.getNewPath())
                .oldPath(src.getOldPath())
                .build();
    }

    /*========================================issue====================================================*/
    public static Issue convertIssue(User author, TGitEventIssue objectAttributes) {
        return Issue.builder()
                .id(objectAttributes.getId())
                .number(objectAttributes.getIid())
                .title(objectAttributes.getTitle())
                .body(objectAttributes.getDescription())
                .link(objectAttributes.getUrl())
                .closed("closed".equals(objectAttributes.getState()))
                .author(author)
                .created(objectAttributes.getCreateAt())
                .updated(objectAttributes.getUpdateAt())
                .milestoneId(objectAttributes.getMilestoneId())
                .state(objectAttributes.getState())
                .build();
    }

    public static Issue convertIssue(TGitIssue from) {
        return Issue.builder()
                .id(from.getId())
                .number(from.getIid())
                .title(from.getTitle())
                .body(from.getDescription())
                .labels(from.getLabels())
                .closed(TGitIssueState.CLOSED.equals(from.getState()))
                .author(convertUser(from.getAuthor()))
                .created(from.getCreatedAt())
                .updated(from.getUpdatedAt())
                .build();
    }

    /*========================================user====================================================*/
    public static User convertUser(TGitUser src) {
        return User.builder()
                .id(src.getId())
                .username(src.getUsername())
                .name(src.getName())
                .avatar(src.getAvatarUrl())
                .build();
    }

    public static User convertUser(TGitAuthor from) {
        return User.builder()
                .id(from.getId())
                .name(from.getName())
                .username(from.getUsername())
                .email(from.getEmail())
                .build();
    }

    public static User convertUser(TGitAssignee assignee) {
        if (assignee == null) {
            return null;
        }
        return User.builder()
                .id(assignee.getId())
                .name(assignee.getName())
                .username(assignee.getUsername())
                .avatar(assignee.getAvatarUrl())
                .email(assignee.getEmail())
                .build();
    }

    private static List<User> convertUser(List<TGitReviewer> reviewers) {
        return reviewers.stream().map(reviewer -> User.builder()
                .name(reviewer.getUsername())
                .avatar(reviewer.getAvatarUrl())
                .email(reviewer.getEmail())
                .build()
        ).collect(Collectors.toList());
    }

    /*========================================comment====================================================*/
    public static Comment convertComment(TGitNote from) {
        return Comment.builder()
                .id(from.getId())
                .body(from.getBody())
                .author(convertUser(from.getAuthor()))
                .build();
    }

    /*========================================milestones====================================================*/
    public static Milestone convertMilestone(TGitMilestone milestone) {
        if (milestone == null) {
            return null;
        }
        return Milestone.builder()
                .id(milestone.getId())
                .iid(milestone.getIid())
                .title(milestone.getTitle())
                .state(milestone.getState())
                .description(milestone.getDescription())
                .dueDate(milestone.getDueDate())
                .build();
    }

    /*========================================review====================================================*/
    public static Review convertReview(TGitReview review) {
        return Review.builder()
                .id(review.getId())
                .iid(review.getIid().intValue())
                .title(review.getTitle())
                .state(convertReviewState(review.getState()))
                .reviewers(convertUser(review.getReviewers()))
                .author(convertUser(review.getAuthor()))
                .link("")
                .build();
    }

    public static Review convertReview(TGitReviewAttributes review) {
        // review 状态
        TGitReviewState state = Arrays.stream(TGitReviewState.values())
                .filter(e -> e.toValue().equals(review.getState()))
                .findFirst().orElse(TGitReviewState.EMPTY);
        return Review.builder()
                .id(review.getId())
                .iid(review.getIid().intValue())
                .title(review.getTitle())
                .state(convertReviewState(state))
                .sourceBranch(review.getSourceBranch())
                .sourceCommit(review.getSourceCommit())
                .sourceProjectId(review.getSourceProjectId())
                .targetBranch(review.getTargetBranch())
                .targetCommit(review.getTargetCommit())
                .targetProjectId(review.getTargetProjectId())
                .link("")
                .build();
    }

    public static ReviewState convertReviewState(TGitReviewState reviewState) {
        switch (reviewState) {
            case APPROVING:
                return ReviewState.APPROVING;
            case APPROVED:
                return ReviewState.APPROVED;
            case CHANGE_DENIED:
                return ReviewState.CHANGE_DENIED;
            case CHANGE_REQUIRED:
                return ReviewState.CHANGES_REQUESTED;
            case EMPTY:
                return ReviewState.EMPTY;
            case CLOSED:
                return ReviewState.CLOSED;
            default:
                return ReviewState.UNKNOWN;
        }
    }
}
