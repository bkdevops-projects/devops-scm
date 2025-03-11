package com.tencent.devops.scm.provider.git.tgit;

import com.tencent.devops.scm.api.PullRequestService;
import com.tencent.devops.scm.api.enums.PullRequestState;
import com.tencent.devops.scm.api.exception.NotFoundScmApiException;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Comment;
import com.tencent.devops.scm.api.pojo.CommentInput;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.PullRequest;
import com.tencent.devops.scm.api.pojo.PullRequestInput;
import com.tencent.devops.scm.api.pojo.PullRequestListOptions;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.sdk.tgit.TGitApi;
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory;
import com.tencent.devops.scm.sdk.tgit.enums.TGitMergeRequestState;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommit;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequest;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequestFilter;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequestParams;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequestParams.TGitMergeRequestParamsBuilder;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitNote;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProject;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class TGitPullRequestService implements PullRequestService {

    private final TGitApiFactory apiFactory;

    public TGitPullRequestService(TGitApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public PullRequest find(ScmProviderRepository repository, Integer number) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitMergeRequest mergeRequest = tGitApi.getMergeRequestApi()
                    .getMergeRequestByIid(repo.getProjectIdOrPath(), number);
            TGitProject targetProject = tGitApi.getProjectApi().getProject(repo.getProjectIdOrPath());
            // 授权者不一定有fork仓库的权限
            TGitProject sourceProject =
                    mergeRequest.getTargetProjectId().equals(mergeRequest.getSourceProjectId()) ? targetProject : null;
            return TGitObjectConverter.convertPullRequest(mergeRequest, sourceProject, targetProject);
        });
    }

    @Override
    public List<PullRequest> list(ScmProviderRepository repository, PullRequestListOptions opts) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitMergeRequestFilter filter = TGitMergeRequestFilter.builder()
                    .state(convertFromState(opts.getState()))
                    .sourceBranch(opts.getSourceBranch())
                    .targetBranch(opts.getTargetBranch())
                    .page(opts.getPage())
                    .perPage(opts.getPageSize())
                    .build();
            List<TGitMergeRequest> mergeRequests = tGitApi.getMergeRequestApi()
                    .getMergeRequests(repo.getProjectIdOrPath(), filter);
            TGitProject targetProject = tGitApi.getProjectApi().getProject(repo.getProjectIdOrPath());
            return mergeRequests.stream()
                    .map(mergeRequest ->
                            TGitObjectConverter.convertPullRequest(mergeRequest, null, targetProject)
                    )
                    .collect(Collectors.toList());
        });
    }

    @Override
    public PullRequest create(ScmProviderRepository repository, PullRequestInput input) {
        if (StringUtils.isEmpty(input.getTitle())) {
            throw new IllegalArgumentException("title cannot be empty");
        }
        if (StringUtils.isEmpty(input.getSourceBranch())) {
            throw new IllegalArgumentException("source branch cannot be empty");
        }
        if (StringUtils.isEmpty(input.getTargetBranch())) {
            throw new IllegalArgumentException("target branch cannot be empty");
        }

        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitMergeRequestParamsBuilder builder = TGitMergeRequestParams.builder()
                    .title(input.getTitle())
                    .description(input.getBody())
                    .sourceBranch(input.getSourceBranch())
                    .targetBranch(input.getTargetBranch());
            if (input.getTargetRepo() != null && input.getTargetRepo() instanceof Long) {
                builder.targetProjectId((Long) input.getTargetRepo());
            }
            TGitMergeRequest mergeRequest = tGitApi.getMergeRequestApi()
                    .createMergeRequest(repo.getProjectIdOrPath(), builder.build());
            TGitProject targetProject = tGitApi.getProjectApi().getProject(mergeRequest.getTargetProjectId());
            TGitProject sourceProject =
                    mergeRequest.getTargetProjectId().equals(mergeRequest.getSourceProjectId()) ? targetProject
                            : tGitApi.getProjectApi().getProject(mergeRequest.getSourceProjectId());
            ;
            return TGitObjectConverter.convertPullRequest(mergeRequest, sourceProject, targetProject);
        });
    }

    @Override
    public List<Change> listChanges(ScmProviderRepository repository, Integer number, ListOptions opts) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitMergeRequest mergeRequest = getMergeRequestByIid(tGitApi, repo.getProjectIdOrPath(), number);
            TGitMergeRequest mergeRequestChanges = tGitApi.getMergeRequestApi()
                    .getMergeRequestChanges(repo.getProjectIdOrPath(), mergeRequest.getId());
            return mergeRequestChanges.getFiles().stream()
                    .map(TGitObjectConverter::convertChange)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public List<Commit> listCommits(ScmProviderRepository repository, Integer number, ListOptions opts) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitMergeRequest mergeRequest = getMergeRequestByIid(tGitApi, repo.getProjectIdOrPath(), number);
            List<TGitCommit> mergeRequestCommits = tGitApi.getMergeRequestApi()
                    .getMergeRequestCommits(repo.getProjectIdOrPath(), mergeRequest.getId(),
                            opts.getPage(), opts.getPageSize());
            return mergeRequestCommits.stream()
                    .map(TGitObjectConverter::convertCommit)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public void merge(ScmProviderRepository repository, Integer number) {
        TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitMergeRequest mergeRequest = getMergeRequestByIid(tGitApi, repo.getProjectIdOrPath(), number);
            tGitApi.getMergeRequestApi().mergeMergeRequest(repo.getProjectIdOrPath(), mergeRequest.getId());
        });
    }

    @Override
    public void close(ScmProviderRepository repository, Integer number) {
        TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitMergeRequest mergeRequest = getMergeRequestByIid(tGitApi, repo.getProjectIdOrPath(), number);
            tGitApi.getMergeRequestApi().closeMergeRequest(repo.getProjectIdOrPath(), mergeRequest.getId());
        });
    }

    @Override
    public Comment findComment(ScmProviderRepository repository, Integer number, Long commentId) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitMergeRequest mergeRequest = getMergeRequestByIid(tGitApi, repo.getProjectIdOrPath(), number);
            TGitNote mergeRequestNote = tGitApi.getNotesApi()
                    .getMergeRequestNote(repo.getProjectIdOrPath(), mergeRequest.getId(), commentId);
            return TGitObjectConverter.convertComment(mergeRequestNote);
        });
    }

    @Override
    public List<Comment> listComments(ScmProviderRepository repository, Integer number, ListOptions opts) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitMergeRequest mergeRequest = getMergeRequestByIid(tGitApi, repo.getProjectIdOrPath(), number);
            List<TGitNote> mergeRequestNotes = tGitApi.getNotesApi()
                    .getMergeRequestNotes(repo.getProjectIdOrPath(), mergeRequest.getId(),
                            opts.getPage(), opts.getPageSize());

            return mergeRequestNotes.stream()
                    .map(TGitObjectConverter::convertComment)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public Comment createComment(ScmProviderRepository repository, Integer number, CommentInput input) {
        if (StringUtils.isBlank(input.getBody())) {
            throw new IllegalArgumentException("body cannot be empty");
        }
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitMergeRequest mergeRequest = getMergeRequestByIid(tGitApi, repo.getProjectIdOrPath(), number);
            TGitNote mergeRequestNote = tGitApi.getNotesApi()
                    .createMergeRequestNote(repo.getProjectIdOrPath(), mergeRequest.getId(), input.getBody());
            return TGitObjectConverter.convertComment(mergeRequestNote);
        });
    }

    @Override
    public void deleteComment(ScmProviderRepository repository, Integer number, Long commentId) {
    }

    private TGitMergeRequestState convertFromState(PullRequestState from) {
        if (from == null) {
            return null;
        }
        TGitMergeRequestState state;
        switch (from) {
            case OPENED:
                state = TGitMergeRequestState.OPENED;
                break;
            case REOPENED:
                state = TGitMergeRequestState.REOPENED;
                break;
            case MERGED:
                state = TGitMergeRequestState.MERGED;
                break;
            case CLOSED:
                state = TGitMergeRequestState.CLOSED;
                break;
            default:
                return null;
        }
        return state;
    }

    private TGitMergeRequest getMergeRequestByIid(TGitApi tGitApi, Object projectIdOrPath, Integer number) {
        TGitMergeRequest mergeRequest = tGitApi.getMergeRequestApi().getMergeRequestByIid(projectIdOrPath, number);
        if (mergeRequest == null) {
            throw new NotFoundScmApiException(String.format("merge request %s not found", number));
        }
        return mergeRequest;
    }
}
