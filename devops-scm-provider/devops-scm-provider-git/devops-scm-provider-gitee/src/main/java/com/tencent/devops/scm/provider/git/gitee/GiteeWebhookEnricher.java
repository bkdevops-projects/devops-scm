package com.tencent.devops.scm.provider.git.gitee;

import static com.tencent.devops.scm.api.enums.EventAction.PUSH_FILE;

import com.tencent.devops.scm.api.WebhookEnricher;
import com.tencent.devops.scm.api.exception.UnAuthorizedScmApiException;
import com.tencent.devops.scm.api.function.TriConsumer;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.PullRequest;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook;
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook;
import com.tencent.devops.scm.provider.git.gitee.auth.GiteeTokenAuthProviderAdapter;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeApiException;
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCommit;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCommitCompare;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCommitDetail;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequest;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequestDiff;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class GiteeWebhookEnricher implements WebhookEnricher {
    private final Map<Class<? extends Webhook>, TriConsumer<GiteeApi, GitScmProviderRepository, Webhook>> eventActions;

    private final GiteeApiFactory apiFactory;

    public GiteeWebhookEnricher(GiteeApiFactory apiFactory) {
        this.apiFactory = apiFactory;
        this.eventActions = new HashMap<>();
        eventActions.put(PullRequestHook.class, this::enrichPullRequestHook);
        eventActions.put(GitPushHook.class, this::enrichPushHook);
    }

    private void enrichPushHook(GiteeApi giteeApi, GitScmProviderRepository repository, Webhook hook) {
        GitPushHook gitPushHook = (GitPushHook) hook;
        List<Change> changes = new ArrayList<>();
        if (PUSH_FILE.equals(gitPushHook.getAction())) {
            GiteeCommitCompare commitCompare = giteeApi.getFileApi().commitCompare(
                    repository.getProjectIdOrPath(),
                    gitPushHook.getBefore(),
                    gitPushHook.getAfter(),
                    false
            );
            changes = GiteeObjectConverter.convertCompare(commitCompare);
        }
        gitPushHook.setChanges(changes);
    }

    private void enrichPullRequestHook(GiteeApi giteeApi, GitScmProviderRepository repository, Webhook hook) {
        PullRequestHook pullRequestHook = (PullRequestHook) hook;
        PullRequest pullRequest = pullRequestHook.getPullRequest();
        // 填充文件变更列表
        GiteePullRequestDiff[] pullRequestDiffs = giteeApi.getPullRequestApi()
                .listChanges(
                        repository.getProjectIdOrPath(),
                        pullRequest.getNumber().longValue()
                );
        List<Change> changes = Arrays.stream(pullRequestDiffs)
                .map(GiteeObjectConverter::convertChange)
                .collect(Collectors.toList());
        pullRequestHook.setChanges(changes);
        // 填充最后提交信息
        Commit requestHookCommit = pullRequestHook.getCommit();
        GiteeCommitDetail commit = giteeApi.getCommitApi().getCommit(
                repository.getProjectIdOrPath(),
                requestHookCommit.getSha()
        );
        requestHookCommit.setMessage(
                        Optional.ofNullable(commit.getCommit())
                                .map(GiteeCommit::getMessage)
                                .orElse("")
                );
        requestHookCommit.setAuthor(GiteeObjectConverter.convertSignature(commit.getAuthor()));
        // 根据完整的MR/Review信息填充变量
        pullRequestHook.getExtras().putAll(
                fillPullRequestReviewVars(
                        giteeApi,
                        repository,
                        pullRequest
                )
        );
    }

    @Override
    public Webhook enrich(ScmProviderRepository repository, Webhook webhook) throws UnAuthorizedScmApiException {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        try {
            GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
            if (eventActions.containsKey(webhook.getClass())) {
                eventActions.get(webhook.getClass()).accept(giteeApi, repo, webhook);
            }
            return webhook;
        } catch (GiteeApiException exception) {
            if (exception.getStatusCode() == 401 || exception.getStatusCode() == 403) {
                throw new UnAuthorizedScmApiException(exception.getMessage());
            } else {
                throw exception;
            }
        }
    }

    public Map<String,Object> fillPullRequestReviewVars(
            GiteeApi giteeApi,
            GitScmProviderRepository repository,
            PullRequest pullRequest
    ) {
        return fillPullRequestVars(giteeApi, repository, pullRequest);
    }

    public Map<String,Object> fillPullRequestVars(
            GiteeApi giteeApi,
            GitScmProviderRepository repository,
            PullRequest pullRequest
    ) {
        GiteePullRequest giteePullRequest = giteeApi.getPullRequestApi().getPullRequest(
                repository.getProjectIdOrPath(),
                pullRequest.getNumber().longValue()
        );
        Map<String, Object> outputs = new HashMap<>();
        if (giteePullRequest != null) {
            pullRequest.setTitle(StringUtils.defaultString(giteePullRequest.getTitle(), ""));
            outputs.putAll(GiteeObjectToMapConverter.convertPullRequest(giteePullRequest));
        }
        return outputs;
    }
}
