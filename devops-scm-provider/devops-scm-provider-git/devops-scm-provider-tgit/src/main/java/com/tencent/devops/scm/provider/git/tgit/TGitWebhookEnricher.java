package com.tencent.devops.scm.provider.git.tgit;

import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_OPERATION_KIND;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.CI_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BASE_REF;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_AUTHOR;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_HEAD_REF;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REF;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA_SHORT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_BRANCH;
import static com.tencent.devops.scm.sdk.tgit.enums.TGitPushOperationKind.UPDATE_NONFASTFORWORD;

import com.tencent.devops.scm.api.WebhookEnricher;
import com.tencent.devops.scm.api.exception.UnAuthorizedScmApiException;
import com.tencent.devops.scm.api.function.PairConsumer;
import com.tencent.devops.scm.api.function.TriConsumer;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.PullRequest;
import com.tencent.devops.scm.api.pojo.Review;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;
import com.tencent.devops.scm.api.pojo.webhook.git.AbstractCommentHook;
import com.tencent.devops.scm.api.pojo.webhook.git.CommitCommentHook;
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook;
import com.tencent.devops.scm.api.pojo.webhook.git.IssueCommentHook;
import com.tencent.devops.scm.api.pojo.webhook.git.IssueHook;
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestCommentHook;
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook;
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestReviewHook;
import com.tencent.devops.scm.provider.git.tgit.auth.TGitAuthProviderFactory;
import com.tencent.devops.scm.sdk.tgit.TGitApi;
import com.tencent.devops.scm.sdk.tgit.TGitApiException;
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommit;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitDiff;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequest;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProject;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitReview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TGitWebhookEnricher implements WebhookEnricher {

    private final TGitApiFactory apiFactory;
    private final Map<Class<? extends Webhook>, TriConsumer<TGitApi, GitScmProviderRepository, Webhook>> eventActions;

    private static final Logger logger = LoggerFactory.getLogger(TGitApiException.class);

    public TGitWebhookEnricher(TGitApiFactory apiFactory) {
        this.apiFactory = apiFactory;
        eventActions = new HashMap<>();
        eventActions.put(PullRequestHook.class, this::enrichPullRequestHook);
        eventActions.put(GitPushHook.class, this::enrichPushHook);
        eventActions.put(IssueHook.class, this::enrichIssueHook);
        eventActions.put(PullRequestReviewHook.class, this::enrichPullRequestViewHook);
        eventActions.put(CommitCommentHook.class, this::enrichNoteHook);
        eventActions.put(IssueCommentHook.class, this::enrichNoteHook);
        eventActions.put(PullRequestCommentHook.class, this::enrichNoteHook);
    }

    @Override
    public Webhook enrich(ScmProviderRepository repository, Webhook webhook) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        TGitApi tGitApi = apiFactory.fromAuthProvider(TGitAuthProviderFactory.create(repo.getAuth()));
        try {
            enrichServerRepository(tGitApi, repo, webhook);

            if (eventActions.containsKey(webhook.getClass())) {
                eventActions.get(webhook.getClass()).accept(tGitApi, repo, webhook);
            }
            return webhook;
        } catch (TGitApiException exception) {
            if (exception.getStatusCode() == 401 || exception.getStatusCode() == 403) {
                throw new UnAuthorizedScmApiException(exception.getMessage());
            } else {
                throw exception;
            }
        }
    }

    private void enrichServerRepository(TGitApi tGitApi, GitScmProviderRepository repo, Webhook webhook) {
        TGitProject project = tGitApi.getProjectApi().getProject(repo.getProjectIdOrPath());
        GitScmServerRepository serverRepository = (GitScmServerRepository) webhook.repository();
        serverRepository.setDefaultBranch(project.getDefaultBranch());
        serverRepository.setArchived(project.getArchived());
    }

    private void enrichPullRequestHook(TGitApi tGitApi, GitScmProviderRepository repository, Webhook webhook) {
        PullRequestHook pullRequestHook = (PullRequestHook) webhook;
        PullRequest pullRequest = pullRequestHook.getPullRequest();
        TGitMergeRequest mergeRequest = tGitApi.getMergeRequestApi()
                .getMergeRequestChanges(repository.getProjectIdOrPath(), pullRequest.getId());
        if (mergeRequest != null) {
            List<Change> changes = mergeRequest.getFiles()
                    .stream()
                    .map(TGitObjectConverter::convertChange)
                    .collect(Collectors.toList());
            pullRequestHook.setChanges(changes);
        }
        // 根据完整的MR/Review信息填充变量
        pullRequestHook.getExtras().putAll(
                fillPullRequestReviewVars(
                        tGitApi,
                        repository,
                        pullRequest,
                        ((PullRequestHook) webhook).getRepo()
                )
        );
    }

    private void enrichPushHook(TGitApi tGitApi, GitScmProviderRepository repository, Webhook webhook) {
        GitPushHook pushHook = (GitPushHook) webhook;
        String operationKind = (String) pushHook.getExtras().get(BK_REPO_GIT_WEBHOOK_PUSH_OPERATION_KIND);
        if (UPDATE_NONFASTFORWORD.value.equals(operationKind)) {
            List<TGitDiff> diffs = tGitApi.getRepositoryFileApi()
                    .getFileChange(
                            repository.getProjectIdOrPath(),
                            pushHook.getAfter(),
                            pushHook.getBefore(),
                            false,
                            false,
                            1,
                            1000
                    );
            if (CollectionUtils.isEmpty(diffs)) {
                return;
            }
            pushHook.setChanges(diffs.stream()
                    .map(TGitObjectConverter::convertChange)
                    .collect(Collectors.toList()));
        }
    }

    private void enrichIssueHook(TGitApi tGitApi, GitScmProviderRepository repository, Webhook webhook) {
        IssueHook issueHook = (IssueHook) webhook;
        if (issueHook.getExtras() == null) {
            issueHook.setExtras(new HashMap<>());
        }
        issueHook.getExtras().putAll(fillDefaultBranchVars(tGitApi, repository, webhook, null));
    }

    private void enrichNoteHook(TGitApi tGitApi, GitScmProviderRepository repository, Webhook webhook) {
        AbstractCommentHook commentHook = (AbstractCommentHook) webhook;
        if (commentHook.getExtras() == null) {
            commentHook.setExtras(new HashMap<>());
        }
        // 默认分支的其他信息
        Map<String, Object> extras = commentHook.getExtras();
        Map<String, String> defaultBranchVars = fillDefaultBranchVars(
                tGitApi,
                repository,
                webhook,
                (defaultBranch, commit) -> {
                    if (defaultBranch != null && commit != null) {
                        extras.put(
                                PIPELINE_GIT_COMMIT_MESSAGE,
                                StringUtils.defaultString(commit.getMessage(), "")
                        );
                    }
                }
        );
        extras.putAll(defaultBranchVars);
        if (commentHook instanceof PullRequestCommentHook
                && ((PullRequestCommentHook) commentHook).getPullRequest() != null) {
            // 仅与MR关联的Review事件才需调用API
            PullRequest pullRequest = ((PullRequestCommentHook) commentHook).getPullRequest();
            extras.putAll(
                    fillPullRequestReviewVars(
                            tGitApi,
                            repository,
                            pullRequest,
                            ((PullRequestCommentHook) webhook).getRepo()
                    )
            );
        }
        commentHook.setExtras(extras);
    }

    private void enrichPullRequestViewHook(
            TGitApi tGitApi,
            GitScmProviderRepository repository,
            Webhook webhook
    ) {
        PullRequestReviewHook reviewHook = (PullRequestReviewHook) webhook;
        PullRequest pullRequest = reviewHook.getPullRequest();
        Map<String, Object> extras = reviewHook.getExtras();
        if (pullRequest != null) {
            extras.putAll(
                    fillPullRequestReviewVars(
                            tGitApi,
                            repository,
                            pullRequest,
                            ((PullRequestReviewHook) webhook).getRepo()
                    )
            );
        } else {
            Review review = reviewHook.getReview();
            TGitReview commitReview = tGitApi.getReviewApi()
                    .getCommitReview(
                            repository.getProjectIdOrPath(),
                            review.getId()
                    );
            review.setTitle(StringUtils.defaultString(commitReview.getTitle(), ""));
        }
        // 默认分支信息
        Map<String, String> defaultBranchVars = fillDefaultBranchVars(tGitApi, repository, webhook,
                (defaultBranch, commit) -> {
                    if (defaultBranch != null && commit != null) {
                        extras.put(
                                PIPELINE_GIT_COMMIT_MESSAGE,
                                StringUtils.defaultString(commit.getMessage(), "")
                        );
                    }
                }
        );
        extras.putAll(defaultBranchVars);
        // 目前BASE_REF和HEAD_REF分别代表目标分支和源分支
        extras.put(PIPELINE_GIT_BASE_REF, extras.get(BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH));
        extras.put(PIPELINE_GIT_HEAD_REF, extras.get(BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH));
    }

    /**
     * 填充MR变量信息
     * @param pullRequest pull request 基础信息，webhook body中获取的基础信息
     */
    private Map<String, Object> fillPullRequestVars(
            TGitApi tGitApi,
            GitScmProviderRepository repository,
            PullRequest pullRequest,
            GitScmServerRepository scmServerRepository
    ) {
        TGitMergeRequest mergeRequestInfo = tGitApi.getMergeRequestApi()
                .getMergeRequestById(
                        repository.getProjectIdOrPath(),
                        pullRequest.getId()
                );
        Map<String, Object> outputs = new HashMap<>();
        if (mergeRequestInfo != null) {
            pullRequest.setTitle(StringUtils.defaultString(mergeRequestInfo.getTitle(), ""));
            outputs.putAll(TGitObjectToMapConverter.convertPullRequest(mergeRequestInfo, scmServerRepository));
        }
        return outputs;
    }

    /**
     * 填充与Mr关联的Review参数
     */
    private Map<String, Object> fillPullRequestReviewVars(
            TGitApi tGitApi,
            GitScmProviderRepository repository,
            PullRequest pullRequest,
            GitScmServerRepository scmServerRepository
    ) {
        Map<String, Object> vars = new HashMap<>(
                fillPullRequestVars(
                        tGitApi,
                        repository,
                        pullRequest,
                        scmServerRepository
                )
        );
        TGitReview tGitMergeRequestReviewInfo = tGitApi.getReviewApi()
                .getMergeRequestReview(
                        repository.getProjectIdOrPath(),
                        pullRequest.getId()
                );
        if (tGitMergeRequestReviewInfo != null) {
            vars.putAll(TGitObjectToMapConverter.convertReview(tGitMergeRequestReviewInfo));
        }
        return vars;
    }

    /**
     * 获取默认分支变量
     * @param tGitApi API调用
     * @param repository 仓库信息
     * @param webhook webhook信息
     * @param extAction 扩展action, 用于扩展默认分支和commit信息
     * @return 默认分支和commit信息（基础变量，不包含扩展字段，扩展字段需在外部手动组装）
     */
    private Map<String, String> fillDefaultBranchVars(
            TGitApi tGitApi,
            GitScmProviderRepository repository,
            Webhook webhook,
            PairConsumer<String, TGitCommit> extAction
    ) {
        Map<String, String> extra = new HashMap<>();
        enrichDefaultBranchAndCommitInfo(
                tGitApi,
                repository,
                webhook,
                (defaultBranch, commit) -> {
                    if (defaultBranch != null && commit != null) {
                        extra.put(PIPELINE_GIT_REF, defaultBranch);
                        extra.put(CI_BRANCH, defaultBranch);
                        extra.put(PIPELINE_WEBHOOK_BRANCH, defaultBranch);
                        extra.put(PIPELINE_GIT_COMMIT_AUTHOR, commit.getAuthorName());
                        extra.put(PIPELINE_GIT_SHA, commit.getId());
                        extra.put(PIPELINE_GIT_SHA_SHORT, commit.getShortId());
                        // 额外字段，不同事件默认分支参数不同有差异，例如：Note 和 Issue
                        if (extAction != null) {
                            extAction.accept(defaultBranch, commit);
                        }
                    }
                }
        );
        return extra;
    }

    private void enrichDefaultBranchAndCommitInfo(
            TGitApi tGitApi,
            GitScmProviderRepository repository,
            Webhook webhook,
            PairConsumer<String, TGitCommit> action
    ) {
        GitScmServerRepository serverRepository = (GitScmServerRepository) webhook.repository();
        String defaultBranch = serverRepository.getDefaultBranch();
        TGitCommit commit = null;
        if (defaultBranch != null) {
            commit = tGitApi.getCommitsApi().getCommit(repository.getProjectIdOrPath(), defaultBranch);
        }
        action.accept(defaultBranch, commit);
    }
}
