package com.tencent.devops.scm.provider.git.gitee;

import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_ACTION;

import com.tencent.devops.scm.api.WebhookParser;
import com.tencent.devops.scm.api.enums.EventAction;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.HookRequest;
import com.tencent.devops.scm.api.pojo.PullRequest;
import com.tencent.devops.scm.api.pojo.Signature;
import com.tencent.devops.scm.api.pojo.User;
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
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventRef;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteePullRequestHook;
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteePushHook;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import kotlin.Pair;
import org.apache.commons.collections4.CollectionUtils;

public class GiteeWebhookParser implements WebhookParser {

    // 忽略的pull request动作, Gitee 新建PR时会瞬间发送三个webhook动作，对无效的webhook进行过滤
    private static final List<Pair<String, String>> IGNORED_PULL_REQUEST_ACTION = List.of(
            new Pair<>(EventAction.OPEN.value, "test"),
            new Pair<>(EventAction.OPEN.value, "assign")
    );

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
        // 删除分支时没有headCommit
        GiteeEventCommit headCommit = giteePushHook.getHeadCommit();
        Commit commit = Optional.ofNullable(headCommit)
                .map(GiteeObjectConverter::convertCommit)
                .orElse(null);
        if (giteePushHook.getCreated()) {
            action = EventAction.NEW_BRANCH;
        } else if (giteePushHook.getDeleted()) {
            action = EventAction.DELETE;
            if (commit == null) {
                commit = Commit.builder()
                        .sha(giteePushHook.getBefore())
                        .message("")
                        .build();
            }
        }
        String ref = GitUtils.trimRef(giteePushHook.getRef());
        String link;
        // 根据事件动作封装事件详情链接
        switch (action) {
            case NEW_BRANCH:
                link = (new StringBuilder())
                        .append(repository.getWebUrl())
                        .append("/tree/")
                        .append(ref)
                        .toString();
                break;
            case DELETE:
                link = repository.getWebUrl();
                break;
            default:
                link = commit.getLink();
        }
        return GitPushHook.builder()
                .action(action)
                .ref(ref)
                .repo(repository)
                .eventType(GiteeEventType.PUSH.name())
                .before(giteePushHook.getBefore())
                .after(giteePushHook.getAfter())
                .commit(commit)
                .link(link)
                .sender(GiteeObjectConverter.convertUser(giteePushHook.getSender()))
                .commits(
                        CollectionUtils.emptyIfNull(giteePushHook.getCommits())
                        .stream()
                        .map(GiteeObjectConverter::convertCommit)
                        .collect(Collectors.toList())
                )
                .totalCommitsCount(
                        Optional.ofNullable(giteePushHook.getTotalCommitsCount())
                        .map(Long::intValue)
                        .orElse(0)
                )
                .extras(new HashMap<>())
                .build();
    }

    private Webhook parsePullRequestHook(String body) {
        GiteePullRequestHook giteePullRequestHook = ScmJsonUtil.fromJson(body, GiteePullRequestHook.class);
        GitScmServerRepository repository = GiteeObjectConverter.convertRepository(
                giteePullRequestHook.getRepository()
        );
        GiteeEventPullRequest sourcePullRequest = giteePullRequestHook.getPullRequest();
        GiteeEventRef head = sourcePullRequest.getHead();
        PullRequest pullRequest = PullRequest.builder()
                .id(sourcePullRequest.getId())
                .number(sourcePullRequest.getNumber().intValue())
                .sourceRepo(
                        GiteeObjectConverter.convertRepository(
                                giteePullRequestHook.getSourceRepo().getRepository()
                        )
                )
                .sourceRef(GiteeObjectConverter.convertRef(head))
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
        String hookAction = giteePullRequestHook.getAction();
        String hookActionDesc = giteePullRequestHook.getActionDesc();
        // 无效的hook消息跳过CI流程
        boolean skipCi = isIgnorePullRequestAction(hookAction, hookActionDesc);
        HashMap<String, Object> extras = new HashMap<>();
        EventAction action = GiteeObjectConverter.convertAction(
                hookAction,
                hookActionDesc
        );
        // 汉化需要, [更新] → [编辑]
        if (EventAction.UPDATE.equals(action)) {
            action = EventAction.EDIT;
        }
        extras.put(PIPELINE_GIT_MR_ACTION, action);
        return PullRequestHook.builder()
                .repo(repository)
                .action(action)
                .eventType(GiteeEventType.MERGE_REQUEST.name())
                .pullRequest(pullRequest)
                .sender(GiteeObjectConverter.convertUser(giteePullRequestHook.getSender()))
                .commit(Commit.builder().sha(head.getSha()).build())
                .extras(extras)
                .skipCi(skipCi)
                .build();
    }

    @Override
    public boolean verify(HookRequest request, String secretToken) {
        return true;
    }

    public static boolean isIgnorePullRequestAction(String action, String actionDesc) {
        return IGNORED_PULL_REQUEST_ACTION.stream()
                .anyMatch(pair -> pair.getFirst().equals(action) && pair.getSecond().equals(actionDesc));
    }
}
