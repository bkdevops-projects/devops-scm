package com.tencent.devops.scm.provider.git.tgit;

import com.tencent.devops.scm.api.RepositoryService;
import com.tencent.devops.scm.api.enums.StatusState;
import com.tencent.devops.scm.api.pojo.Hook;
import com.tencent.devops.scm.api.pojo.HookEvents;
import com.tencent.devops.scm.api.pojo.HookEvents.HookEventsBuilder;
import com.tencent.devops.scm.api.pojo.HookInput;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.Perm;
import com.tencent.devops.scm.api.pojo.RepoListOptions;
import com.tencent.devops.scm.api.pojo.Status;
import com.tencent.devops.scm.api.pojo.StatusInput;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory;
import com.tencent.devops.scm.sdk.tgit.enums.TGitAccessLevel;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommitStatus;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMember;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProject;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProjectHook;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProjectHook.TGitProjectHookBuilder;
import java.util.List;
import java.util.stream.Collectors;

public class TGitRepositoryService implements RepositoryService {

    private final TGitApiFactory apiFactory;

    public TGitRepositoryService(TGitApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public GitScmServerRepository find(ScmProviderRepository repository) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitProject project = tGitApi.getProjectApi().getProject(repo.getProjectIdOrPath());
            return TGitObjectConverter.convertRepository(project);
        });
    }

    @Override
    public Perm findPerms(ScmProviderRepository repository, String username) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            Perm perm;
            try {
                TGitMember member = tGitApi.getProjectApi().getMember(repo.getProjectIdOrPath(), username);
                if (member == null) {
                    perm = Perm.builder().pull(true).push(false).admin(false).build();
                } else {
                    perm = Perm.builder().pull(true).push(canPush(member.getAccessLevel()))
                            .admin(canAdmin(member.getAccessLevel())).build();
                }
            } catch (Exception e) {
                perm = Perm.builder().pull(false).push(false).admin(false).build();
            }
            return perm;
        });
    }

    @Override
    public List<ScmServerRepository> list(IScmAuth auth, RepoListOptions opts) {
        return TGitApiTemplate.execute(auth, apiFactory, (tGitApi) -> {
            List<TGitProject> projects = tGitApi.getProjectApi()
                    .getProjects(opts.getRepoName(), opts.getPage(), opts.getPageSize());
            return projects.stream()
                    .map(TGitObjectConverter::convertRepository)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public List<Hook> listHooks(ScmProviderRepository repository, ListOptions opts) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            List<TGitProjectHook> hooks = tGitApi.getProjectApi()
                    .getHooks(repo.getProjectIdOrPath(), opts.getPage(), opts.getPageSize());
            return hooks.stream().map(this::convertHook).collect(Collectors.toList());
        });
    }

    @Override
    public Hook createHook(ScmProviderRepository repository, HookInput input) {
        if (input.getUrl() == null) {
            throw new IllegalArgumentException("url can not empty");
        }
        if (input.getEvents() == null) {
            throw new IllegalArgumentException("events can not empty");
        }
        TGitProjectHook tGitProjectHook = convertFromHookInput(input);

        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitProjectHook hook = tGitApi.getProjectApi()
                    .addHook(repo.getProjectIdOrPath(), tGitProjectHook, input.getSecret());
            return convertHook(hook);
        });
    }

    @Override
    public Hook updateHook(ScmProviderRepository repository, long hookId, HookInput input) {
        TGitProjectHook tGitProjectHook = convertFromHookInput(input);

        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitProjectHook hook = tGitApi.getProjectApi()
                    .updateHook(repo.getProjectIdOrPath(), hookId, tGitProjectHook, input.getSecret());
            return convertHook(hook);
        });
    }

    @Override
    public void deleteHook(ScmProviderRepository repository, long hookId) {
        TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            tGitApi.getProjectApi().deleteHook(repo.getProjectIdOrPath(), hookId);
        });
    }

    @Override
    public Hook getHook(ScmProviderRepository repository, long hookId) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitProjectHook hook = tGitApi.getProjectApi().getHook(repo.getProjectIdOrPath(), hookId);
            return convertHook(hook);
        });
    }

    @Override
    public List<Status> listStatus(ScmProviderRepository repository, String ref, ListOptions opts) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            List<TGitCommitStatus> commitStatuses = tGitApi.getCommitsApi()
                    .getCommitStatuses(repo.getProjectIdOrPath(), ref, opts.getPage(), opts.getPageSize());
            return commitStatuses.stream()
                    .map(this::convertStatus)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public Status createStatus(ScmProviderRepository repository, String ref, StatusInput input) {
        return null;
    }

    private boolean canPush(TGitAccessLevel accessLevel) {
        return accessLevel.value >= TGitAccessLevel.DEVELOPER.value;
    }

    private boolean canAdmin(TGitAccessLevel accessLevel) {
        return accessLevel.value >= TGitAccessLevel.MASTER.value;
    }

    private Hook convertHook(TGitProjectHook from) {
        return Hook.builder()
                .id(from.getId())
                .url(from.getUrl())
                .active(true)
                .events(convertEvents(from))
                .build();
    }

    private HookEvents convertEvents(TGitProjectHook from) {
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
        if (from.getReviewEvents()) {
            builder.pullRequestReview(true);
        }
        return builder.build();
    }

    private TGitProjectHook convertFromHookInput(HookInput input) {
        TGitProjectHookBuilder builder = TGitProjectHook.builder();
        builder.url(input.getUrl());
        HookEvents events = input.getEvents();
        if (events.getPush() != null && events.getPush()) {
            builder.pushEvents(true);
        }
        if (events.getTag() != null && events.getTag()) {
            builder.tagPushEvents(true);
        }
        if (events.getPullRequest() != null && events.getPullRequest()) {
            builder.mergeRequestsEvents(true);
        }
        if (events.getIssue() != null && events.getIssue()) {
            builder.issuesEvents(true);
        }
        if (enableNoteEvent(events)) {
            builder.noteEvents(true);
        }
        if (events.getPullRequestReview() != null && events.getPullRequestReview()) {
            builder.reviewEvents(true);
        }
        return builder.build();
    }

    private Boolean enableNoteEvent(HookEvents events) {
        return (events.getPullRequestComment() != null && events.getPullRequestComment())
                || (events.getIssueComment() != null && events.getIssueComment());
    }

    private Status convertStatus(TGitCommitStatus from) {
        return Status.builder()
                .state(convertState(from.getState()))
                .context(from.getContext())
                .desc(from.getDescription())
                .targetUrl(from.getTargetUrl())
                .build();
    }

    private StatusState convertState(String from) {
        StatusState state;
        switch (from) {
            case "pending":
                state = StatusState.PENDING;
                break;
            case "success":
                state = StatusState.SUCCESS;
                break;
            case "error":
                state = StatusState.ERROR;
                break;
            case "failure":
                state = StatusState.FAILURE;
                break;
            default:
                state = StatusState.UNKNOWN;
                break;
        }
        return state;
    }

}
