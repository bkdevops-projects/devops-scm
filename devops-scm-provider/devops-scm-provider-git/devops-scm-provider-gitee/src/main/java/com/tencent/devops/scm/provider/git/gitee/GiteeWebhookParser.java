package com.tencent.devops.scm.provider.git.gitee;

import com.tencent.devops.scm.api.WebhookParser;
import com.tencent.devops.scm.api.enums.EventAction;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.HookRequest;
import com.tencent.devops.scm.api.pojo.PullRequest;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook;
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook;
import com.tencent.devops.scm.api.util.GitUtils;
import com.tencent.devops.scm.provider.git.gitee.enums.GiteeEventType;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseLabel;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventCommit;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventPullRequest;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteePullRequestHook;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteePushHook;
import java.util.HashMap;
import java.util.stream.Collectors;

public class GiteeWebhookParser implements WebhookParser {

    @Override
    public Webhook parse(HookRequest request) {
        Webhook hook = null;
        switch (request.getHeaders().get("X-Gitee-Event")) {
            case "Push Hook":
                hook = parsePushHook(request.getBody());
                break;
            case "Merge Request Hook":
                hook = parsePullRequestHook(request.getBody());
                break;
            default:

        }
        return hook;
    }

    private Webhook parsePushHook(String body) {
        GiteePushHook giteePushHook = ScmJsonUtil.fromJson(body, GiteePushHook.class);
        GitScmServerRepository repository = GiteeObjectConverter.convertRepository(
                giteePushHook.getRepository()
        );
        EventAction action = EventAction.PUSH_FILE;
        if (giteePushHook.getCreated()) {
            action = EventAction.CREATE;
        } else if (giteePushHook.getDeleted()) {
            action = EventAction.DELETE;
        }
        GiteeEventCommit headCommit = giteePushHook.getHeadCommit();
        return GitPushHook.builder()
                .action(action)
                .ref(GitUtils.trimRef(giteePushHook.getRef()))
                .repo(repository)
                .eventType(GiteeEventType.PUSH.name())
                .before(giteePushHook.getBefore())
                .after(giteePushHook.getAfter())
                .commit(GiteeObjectConverter.convertCommit(headCommit))
                .sender(GiteeObjectConverter.convertUser(giteePushHook.getSender()))
                .commits(giteePushHook.getCommits()
                        .stream()
                        .map(GiteeObjectConverter::convertCommit)
                        .collect(Collectors.toList())
                )
                .totalCommitsCount(giteePushHook.getTotalCommitsCount().intValue())
                .build();
    }

    private Webhook parsePullRequestHook(String body) {
        GiteePullRequestHook giteePullRequestHook = ScmJsonUtil.fromJson(body, GiteePullRequestHook.class);
        GitScmServerRepository repository = GiteeObjectConverter.convertRepository(
                giteePullRequestHook.getRepository()
        );
        GiteeEventPullRequest sourcePullRequest = giteePullRequestHook.getPullRequest();
        PullRequest pullRequest = PullRequest.builder()
                .id(sourcePullRequest.getId())
                .number(sourcePullRequest.getNumber().intValue())
                .sourceRepo(
                        GiteeObjectConverter.convertRepository(
                                giteePullRequestHook.getSourceRepo().getRepository()
                        )
                )
                .sourceRef(GiteeObjectConverter.convertRef(sourcePullRequest.getHead()))
                .targetRef(GiteeObjectConverter.convertRef(sourcePullRequest.getBase()))
                .targetRepo(repository)
                .title(sourcePullRequest.getTitle())
                .body(sourcePullRequest.getBody())
                .description(sourcePullRequest.getBody())
                .link(sourcePullRequest.getHtmlUrl())
                .mergeCommitSha(sourcePullRequest.getMergeCommitSha())
                .merged(sourcePullRequest.getMerged())
                .author(GiteeObjectConverter.convertAuthor(sourcePullRequest.getUser()))
                .created(sourcePullRequest.getCreatedAt())
                .updated(sourcePullRequest.getUpdatedAt())
                .labels(sourcePullRequest.getLabels()
                        .stream()
                        .map(GiteeBaseLabel::getName)
                        .collect(Collectors.toList())
                )
                .milestone(GiteeObjectConverter.convertMilestone(sourcePullRequest.getMilestone()))
                .assignee(sourcePullRequest.getAssignees()
                        .stream().map(GiteeObjectConverter::convertAuthor)
                        .collect(Collectors.toList())
                )
                .baseCommit(sourcePullRequest.getBase().getSha())
                .build();
        return PullRequestHook.builder()
                .repo(repository)
                .action(GiteeObjectConverter.convertAction(giteePullRequestHook.getAction()))
                .eventType(GiteeEventType.MERGE_REQUEST.toValue())
                .pullRequest(pullRequest)
                .commit(Commit.builder().sha(sourcePullRequest.getHead().getSha()).build())
                .extras(new HashMap<>())
                .build();
    }

    @Override
    public boolean verify(HookRequest request, String secretToken) {
        return true;
    }
}
