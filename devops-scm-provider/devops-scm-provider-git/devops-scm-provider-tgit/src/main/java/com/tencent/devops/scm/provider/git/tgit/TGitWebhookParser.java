package com.tencent.devops.scm.provider.git.tgit;

import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_MANUAL_UNLOCK;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_STATE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_ACTION_KIND;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_OPERATION_KIND;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_TOTAL_COMMIT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_APPROVED_REVIEWERS;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_APPROVING_REVIEWERS;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWABLE_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWABLE_TYPE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWERS;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_TAG_CREATE_FROM;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_TAG_OPERATION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA_SHORT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_AUTHOR;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_ACTION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_TAG_FROM;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_TAG_MESSAGE;
import static com.tencent.devops.scm.sdk.tgit.enums.TGitPushOperationKind.UPDATE_NONFASTFORWORD;

import com.tencent.devops.scm.api.WebhookParser;
import com.tencent.devops.scm.api.enums.EventAction;
import com.tencent.devops.scm.api.enums.ReviewState;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Comment;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.HookRequest;
import com.tencent.devops.scm.api.pojo.Issue;
import com.tencent.devops.scm.api.pojo.PullRequest;
import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.api.pojo.Review;
import com.tencent.devops.scm.api.pojo.Signature;
import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.api.pojo.repository.git.GitRepositoryUrl;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;
import com.tencent.devops.scm.api.pojo.webhook.git.AbstractCommentHook;
import com.tencent.devops.scm.api.pojo.webhook.git.CommitCommentHook;
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook;
import com.tencent.devops.scm.api.pojo.webhook.git.GitTagHook;
import com.tencent.devops.scm.api.pojo.webhook.git.IssueCommentHook;
import com.tencent.devops.scm.api.pojo.webhook.git.IssueHook;
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestCommentHook;
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook;
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestReviewHook;
import com.tencent.devops.scm.api.util.GitUtils;
import com.tencent.devops.scm.provider.git.tgit.enums.TGitEventType;
import com.tencent.devops.scm.sdk.common.util.UrlConverter;
import com.tencent.devops.scm.sdk.tgit.enums.TGitPushOperationKind;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitUser;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventCommit;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventDiffFile;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventProject;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventRepository;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventReviewer;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitIssueEvent;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitMergeRequestEvent;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitNoteEvent;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitPushEvent;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitReviewEvent;
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitTagPushEvent;
import com.tencent.devops.scm.sdk.tgit.util.TGitDateUtils;
import com.tencent.devops.scm.sdk.tgit.util.TGitJsonUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class TGitWebhookParser implements WebhookParser {
    @Override
    public Webhook parse(HookRequest request) {
        Webhook hook = null;
        switch (request.getHeaders().get("X-Event")) {
            case "Push Hook":
                hook = parsePushHook(request.getBody());
                break;
            case "Tag Push Hook":
                hook = parseTagHook(request.getBody());
                break;
            case "Merge Request Hook":
                hook = parsePullRequestHook(request.getBody());
                break;
            case "Review Hook":
                hook = parsePullRequestReviewHook(request.getBody());
                break;
            case "Issue Hook":
                hook = parseIssueHook(request.getBody());
                break;
            case "Note Hook":
                hook = parseCommentHook(request.getBody());
                break;
            default:

        }
        return hook;
    }

    @Override
    public boolean verify(HookRequest request, String secretToken) {
        // 没有值不需要校验
        if (secretToken == null || secretToken.isEmpty()) {
            return true;
        }
        String token = request.getHeaders().get("X-Token");
        return secretToken.equals(token);
    }

    private Webhook parsePushHook(String body) {
        TGitPushEvent src = TGitJsonUtil.fromJson(body, TGitPushEvent.class);
        return convertPushHook(src);
    }

    private GitTagHook parseTagHook(String body) {
        TGitTagPushEvent src = TGitJsonUtil.fromJson(body, TGitTagPushEvent.class);

        EventAction action = EventAction.CREATE;
        String sha = src.getAfter();
        if (TGitPushOperationKind.DELETE.value.equals(src.getOperationKind())) {
            action = EventAction.DELETE;
            sha = src.getBefore();
        }
        String createFrom = "";
        if (action == EventAction.DELETE) {
            // 删除标签时展示删除前的tag提交点
            createFrom = GitUtils.getShortSha(src.getBefore());
        } else if (StringUtils.isBlank(src.getCreateFrom()) || StringUtils.isNotBlank(src.getCheckoutSha())) {
            createFrom = GitUtils.getShortSha(src.getCheckoutSha());
        } else {
            createFrom = src.getCreateFrom();
        }
        User user = User.builder().id(src.getUserId())
                .name(src.getUserName())
                .email(src.getUserEmail())
                .build();

        GitScmServerRepository repo = TGitObjectConverter.convertRepository(
                src.getProjectId(),
                src.getRepository()
        );

        String refName = GitUtils.trimRef(src.getRef());
        String linkUrl = String.format("%s/-/tags/%s", repo.getWebUrl(), refName);
        Reference ref = Reference.builder()
                .name(refName)
                .sha(sha)
                .linkUrl(linkUrl)
                .build();
        // Tag 最新的commit
        Commit commit = CollectionUtils.emptyIfNull(src.getCommits())
                .stream()
                .findFirst()
                .map(TGitObjectConverter::convertCommit)
                .orElse(null);
        Map<String, Object> extras = fillTagExtra(src);

        return GitTagHook.builder()
                .ref(ref)
                .repo(repo)
                .eventType(TGitEventType.TAG_PUSH.name())
                .sender(user)
                .action(action)
                .commit(commit)
                .extras(extras)
                .createFrom(createFrom)
                .outputCommitIndexVar(true)
                .build();
    }

    private PullRequestHook parsePullRequestHook(String body) {
        TGitMergeRequestEvent src = TGitJsonUtil.fromJson(body, TGitMergeRequestEvent.class);
        TGitMergeRequestEvent.ObjectAttributes objectAttributes = src.getObjectAttributes();

        TGitUser tGitUser = src.getUser();
        User user = User.builder()
                .name(tGitUser.getName())
                .email(tGitUser.getEmail())
                .avatar(tGitUser.getAvatarUrl())
                .build();

        EventAction action = EventAction.EDIT;
        switch (objectAttributes.getAction()) {
            case "open":
                action = EventAction.OPEN;
                break;
            case "close":
                action = EventAction.CLOSE;
                break;
            case "reopen":
                action = EventAction.REOPEN;
                break;
            case "merge":
                action = EventAction.MERGE;
                break;
            case "update":
                if ("push-update".equals(objectAttributes.getExtensionAction())) {
                    action = EventAction.PUSH_UPDATE;
                }
                break;
            default:

        }

        PullRequest pullRequest = TGitObjectConverter.convertPullRequest(user, objectAttributes);
        Commit commit = TGitObjectConverter.convertCommit(objectAttributes.getLastCommit());
        // 补充字段
        Map<String, Object> extras = new HashMap<>();
        extras.put(BK_REPO_GIT_MANUAL_UNLOCK, src.getManualUnlock()); // 是否手动解锁
        extras.put(PIPELINE_GIT_MR_ACTION, src.getObjectAttributes().getAction());

        TGitEventProject srcTarget = objectAttributes.getTarget();
        GitRepositoryUrl targetRepositoryUrl = new GitRepositoryUrl(srcTarget.getHttpUrl());
        GitScmServerRepository repo = GitScmServerRepository.builder()
                .id(objectAttributes.getTargetProjectId())
                .group(targetRepositoryUrl.getGroup())
                .name(srcTarget.getName())
                .fullName(targetRepositoryUrl.getFullName())
                .httpUrl(srcTarget.getHttpUrl())
                .sshUrl(srcTarget.getSshUrl())
                .webUrl(srcTarget.getWebUrl())
                .build();

        return PullRequestHook.builder()
                .action(action)
                .repo(repo)
                .eventType(TGitEventType.MERGE_REQUEST.name())
                .pullRequest(pullRequest)
                .sender(user)
                .commit(commit)
                .extras(extras)
                .build();
    }

    private IssueHook parseIssueHook(String body) {
        TGitIssueEvent src = TGitJsonUtil.fromJson(body, TGitIssueEvent.class);
        TGitIssueEvent.ObjectAttributes objectAttributes = src.getObjectAttributes();

        EventAction action = EventAction.OPEN;
        switch (objectAttributes.getAction()) {
            case "close":
                action = EventAction.CLOSE;
                break;
            case "reopen":
                action = EventAction.REOPEN;
                break;
            case "update":
                action = EventAction.UPDATE;
                break;
            default:
        }

        GitScmServerRepository repo = TGitObjectConverter.convertRepository(
                objectAttributes.getProjectId(),
                src.getRepository()
        );
        // tgit issue repository没有返回httUrl和sshUrl
        repo.setHttpUrl(src.getRepository().getUrl());

        TGitUser tGitUser = src.getUser();
        User sender = User.builder()
                .id(tGitUser.getId())
                .name(tGitUser.getName())
                .avatar(tGitUser.getAvatarUrl())
                .build();

        Issue issue = TGitObjectConverter.convertIssue(sender, src.getObjectAttributes());

        Map<String, Object> extra = new HashMap<>();
        extra.put(BK_REPO_GIT_WEBHOOK_ISSUE_STATE, src.getObjectAttributes().getState());

        return IssueHook.builder()
                .repo(repo)
                .eventType(TGitEventType.ISSUES.name())
                .action(action)
                .issue(issue)
                .sender(sender)
                .extras(extra)
                .build();
    }

    private Webhook parseCommentHook(String body) {
        TGitNoteEvent src = TGitJsonUtil.fromJson(body, TGitNoteEvent.class);
        TGitNoteEvent.ObjectAttributes objectAttributes = src.getObjectAttributes();

        // tgit note repository返回的url是ssh协议
        TGitEventRepository tGitRepo = src.getRepository();
        String httpUrl = tGitRepo.getRealHttpUrl();
        GitRepositoryUrl repositoryUrl = new GitRepositoryUrl(httpUrl);
        GitScmServerRepository repo = GitScmServerRepository.builder()
                .id(src.getProjectId())
                .group(repositoryUrl.getGroup())
                .name(repositoryUrl.getName())
                .fullName(repositoryUrl.getFullName())
                .webUrl(tGitRepo.getHomepage())
                .httpUrl(httpUrl)
                .sshUrl(StringUtils.defaultIfBlank(tGitRepo.getGitSshUrl(), UrlConverter.gitHttp2Ssh(httpUrl)))
                .build();

        TGitUser tGitUser = src.getUser();
        User user = User.builder()
                .id(objectAttributes.getAuthorId())
                .name(tGitUser.getName())
                .avatar(tGitUser.getAvatarUrl())
                .build();

        Comment comment = Comment.builder()
                .id(objectAttributes.getId())
                .body(objectAttributes.getNote())
                .author(user)
                .created(objectAttributes.getCreatedAt())
                .updated(objectAttributes.getUpdatedAt())
                .type(objectAttributes.getNoteableType().value)
                .link(objectAttributes.getUrl())
                .build();

        AbstractCommentHook webhook = null;
        switch (objectAttributes.getNoteableType()) {
            case ISSUE:
                Issue issue = TGitObjectConverter.convertIssue(user, src.getIssue());
                webhook = IssueCommentHook.builder()
                        .eventType(TGitEventType.NOTE.name())
                        .issue(issue)
                        .build();
                break;
            case COMMIT:
                Commit commit = TGitObjectConverter.convertCommit(src.getCommit());
                webhook = CommitCommentHook.builder()
                        .eventType(TGitEventType.NOTE.name())
                        .commit(commit)
                        .build();
                break;
            case REVIEW:
                if (src.getMergeRequest() != null) {
                    PullRequest pullRequest = TGitObjectConverter.convertPullRequest(user, src.getMergeRequest());
                    webhook = PullRequestCommentHook.builder()
                            .eventType(TGitEventType.NOTE.name())
                            .pullRequest(pullRequest)
                            .build();
                } else if (src.getReview() != null) {
                    Review review = TGitObjectConverter.convertReview(src.getReview());
                    webhook = PullRequestCommentHook.builder()
                            .eventType(TGitEventType.NOTE.name())
                            .review(review) // 仅保留重要字段，后续可能用到
                            .build();
                }
                break;
            default:

        }
        if (webhook != null) {
            webhook.setComment(comment);
            webhook.setAction(EventAction.CREATE);
            webhook.setRepo(repo);
            webhook.setSender(user);
        }
        return webhook;
    }

    private Webhook parsePullRequestReviewHook(String body) {
        TGitReviewEvent src = TGitJsonUtil.fromJson(body, TGitReviewEvent.class);

        GitScmServerRepository repo = TGitObjectConverter.convertRepository(src.getProjectId(), src.getRepository());

        TGitEventReviewer eventReviewer = src.getReviewer();
        TGitUser reviewer = eventReviewer.getReviewer();
        User sender = User.builder()
                .id(reviewer.getId())
                .name(reviewer.getName())
                .avatar(reviewer.getAvatarUrl())
                .build();

        ReviewState state;
        boolean closed = false;
        switch (eventReviewer.getState()) {
            case "approving":
                state = ReviewState.APPROVING;
                break;
            case "approved":
                state = ReviewState.APPROVED;
                break;
            case "changes_requested":
                state = ReviewState.CHANGES_REQUESTED;
                break;
            case "change_denied":
                state = ReviewState.CHANGE_DENIED;
                break;
            case "close":
                state = ReviewState.UNKNOWN;
                closed = true;
                break;
            default:
                state = ReviewState.UNKNOWN;
        }

        String link = String.format("%s/reviews/%s", src.getRepository().getHomepage(), src.getIid());
        Review review = Review.builder()
                .id(src.getId())
                .iid(src.getIid())
                .state(state)
                .author(TGitObjectConverter.convertUser(src.getAuthor()))
                .link(link)
                .closed(closed)
                .build();

        Map<String, Object> extra = fillReviewExtra(src);

        PullRequestReviewHook webhook = PullRequestReviewHook.builder()
                        .repo(repo)
                        .action(EventAction.CREATE)
                        .eventType(TGitEventType.REVIEW.name())
                        .review(review)
                        .sender(sender)
                        .extras(extra)
                        .build();
        if ("merge_request".equals(src.getReviewableType())) {
            PullRequest pullRequest = PullRequest.builder()
                    .id(src.getReviewableId())
                    .build();
            webhook.setPullRequest(pullRequest);
        }

        return webhook;
    }

    private GitPushHook convertPushHook(TGitPushEvent src) {
        EventAction action = EventAction.PUSH_FILE;
        if (src.getCreateAndUpdate() != null && !src.getCreateAndUpdate()) {
            action = EventAction.NEW_BRANCH;
        } else if (TGitPushOperationKind.DELETE.value.equals(src.getOperationKind())
                && "0000000000000000000000000000000000000000".equals(src.getAfter())) {
            action = EventAction.DELETE;
        }

        Signature author = new Signature();
        author.setName(src.getUserName());
        author.setEmail(src.getUserEmail());

        Commit commit = Commit.builder()
                .sha(src.getCheckoutSha())
                .author(author)
                .committer(author)
                .build();
        if (!src.getCommits().isEmpty()) {
            TGitEventCommit lastCommit = src.getCommits().get(0);
            commit.setLink(lastCommit.getUrl());
            commit.setMessage(lastCommit.getMessage());
            commit.setCommitTime(TGitDateUtils.convertDateToLocalDateTime(lastCommit.getTimestamp()));
        }

        String operationKind = StringUtils.defaultIfBlank(src.getOperationKind(), "");
        String actionKind = StringUtils.defaultIfBlank(src.getActionKind(), "");
        List<Change> changes = new ArrayList<>();
        // 非强推的情况下，直接读取diffFiles
        if (!UPDATE_NONFASTFORWORD.value.equals(operationKind)) {
            List<TGitEventDiffFile> eventDiffFiles = src.getDiffFiles();
            changes = eventDiffFiles.stream().map(eventDiffFile ->
                    Change.builder()
                            .added(eventDiffFile.getNewFile())
                            .renamed(eventDiffFile.getRenamedFile())
                            .deleted(eventDiffFile.getDeletedFile())
                            .path(eventDiffFile.getNewPath())
                            .oldPath(eventDiffFile.getOldPath())
                            .build()
            ).collect(Collectors.toList());
        }

        Map<String, Object> extras = new HashMap<>();
        extras.put(BK_REPO_GIT_WEBHOOK_PUSH_ACTION_KIND, actionKind);
        extras.put(BK_REPO_GIT_WEBHOOK_PUSH_OPERATION_KIND, operationKind);

        GitScmServerRepository repository = TGitObjectConverter.convertRepository(
                src.getProjectId(),
                src.getRepository()
        );
        User user = User.builder()
                .id(src.getUserId())
                .name(src.getUserName())
                .email(src.getUserEmail())
                .build();

        List<Commit> commits = src.getCommits()
                .stream()
                .map(TGitObjectConverter::convertCommit)
                .collect(Collectors.toList());
        return GitPushHook.builder()
                .action(action)
                .ref(GitUtils.trimRef(src.getRef()))
                .eventType(TGitEventType.PUSH.name())
                .repo(repository)
                .before(src.getBefore())
                .after(src.getAfter())
                .commit(commit)
                .sender(user)
                .commits(commits)
                .changes(changes)
                .totalCommitsCount(src.getTotalCommitsCount())
                .extras(extras)
                .outputCommitIndexVar(true)
                .build();
    }

    private static Map<String, Object> fillTagExtra(TGitTagPushEvent src) {
        Map<String, Object> extras = new HashMap<>();
        extras.put(BK_REPO_GIT_WEBHOOK_TAG_OPERATION, src.getOperationKind());
        extras.put(BK_REPO_GIT_WEBHOOK_PUSH_TOTAL_COMMIT, src.getTotalCommitsCount());
        if (src.getCreateFrom() != null) {
            extras.put(BK_REPO_GIT_WEBHOOK_TAG_CREATE_FROM, src.getCreateFrom());
            extras.put(PIPELINE_GIT_TAG_FROM, src.getCreateFrom());
        }

        extras.put(PIPELINE_GIT_BEFORE_SHA, src.getBefore());
        extras.put(PIPELINE_GIT_BEFORE_SHA_SHORT, GitUtils.getShortSha(src.getBefore()));
        extras.put(PIPELINE_GIT_TAG_MESSAGE, src.getMessage());
        if (src.getCommits() != null && !src.getCommits().isEmpty()) {
            TGitEventCommit lastCommit = src.getCommits().get(0);
            if (lastCommit != null) {
                extras.put(PIPELINE_GIT_COMMIT_AUTHOR, lastCommit.getAuthor().getName());
                extras.put(PIPELINE_GIT_COMMIT_MESSAGE, lastCommit.getMessage());
            }
        }
        return extras;
    }

    private static Map<String, Object> fillReviewExtra(TGitReviewEvent src) {
        Map<String, Object> extra = new HashMap<>();
        extra.put(BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWABLE_ID, src.getReviewableId());
        extra.put(BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWABLE_TYPE, src.getReviewableType());
        List<String> reviewers = new ArrayList<>(8);
        List<String> approvingReviewers = new ArrayList<>(8);
        List<String> approvedReviewers = new ArrayList<>(8);
        src.getReviewers().forEach(it -> {
            reviewers.add(it.getReviewer().getUsername());
            switch (it.getState()) {
                case "approving":
                    approvingReviewers.add(it.getReviewer().getUsername());
                    break;
                case "approved":
                    approvedReviewers.add(it.getReviewer().getUsername());
                    break;
                default:
            }
        });
        extra.put(BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWERS, StringUtils.join(reviewers, ","));
        extra.put(BK_REPO_GIT_WEBHOOK_REVIEW_APPROVING_REVIEWERS, StringUtils.join(approvingReviewers, ","));
        extra.put(BK_REPO_GIT_WEBHOOK_REVIEW_APPROVED_REVIEWERS, StringUtils.join(approvedReviewers, ","));
        return extra;
    }
}
