package com.tencent.devops.scm.provider.git.tgit;

import com.tencent.devops.scm.api.IssueService;
import com.tencent.devops.scm.api.enums.IssueState;
import com.tencent.devops.scm.api.exception.ScmApiException;
import com.tencent.devops.scm.api.pojo.Comment;
import com.tencent.devops.scm.api.pojo.CommentInput;
import com.tencent.devops.scm.api.pojo.Issue;
import com.tencent.devops.scm.api.pojo.IssueInput;
import com.tencent.devops.scm.api.pojo.IssueListOptions;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.sdk.tgit.TGitApi;
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory;
import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueState;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitIssue;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitNote;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class TGitIssueService implements IssueService {

    private final TGitApiFactory apiFactory;

    public TGitIssueService(TGitApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public Issue find(ScmProviderRepository repository, Integer number) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitIssue issue = tGitApi.getIssuesApi().getIssueByIid(repo.getProjectIdOrPath(), number);
            return TGitObjectConverter.convertIssue(issue);
        });
    }

    @Override
    public Issue create(ScmProviderRepository repository, IssueInput input) {
        if (StringUtils.isBlank(input.getTitle())) {
            throw new IllegalArgumentException("issue title cannot be empty");
        }
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitIssue issue = tGitApi.getIssuesApi()
                    .createIssue(repo.getProjectIdOrPath(), input.getTitle(), input.getBody());
            return TGitObjectConverter.convertIssue(issue);
        });
    }

    @Override
    public List<Issue> list(ScmProviderRepository repository, IssueListOptions opts) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            List<TGitIssue> issues = tGitApi.getIssuesApi()
                    .getIssues(repo.getProjectIdOrPath(), convertFromState(opts.getState()), opts.getPage(),
                            opts.getPageSize());
            return issues.stream()
                    .map(TGitObjectConverter::convertIssue)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public void close(ScmProviderRepository repository, Integer number) {
        TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitIssue issue = getIssueIdByNumber(tGitApi, repo.getProjectIdOrPath(), number);
            tGitApi.getIssuesApi().closeIssue(repo.getProjectIdOrPath(), issue.getId());
        });
    }

    @Override
    public Comment findComment(ScmProviderRepository repository, Integer number, Long commentId) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitIssue issue = getIssueIdByNumber(tGitApi, repo.getProjectIdOrPath(), number);
            TGitNote issueNote = tGitApi.getNotesApi()
                    .getIssueNote(repo.getProjectIdOrPath(), issue.getId(), commentId);
            return TGitObjectConverter.convertComment(issueNote);
        });
    }

    @Override
    public List<Comment> listComments(ScmProviderRepository repository, Integer number, ListOptions opts) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitIssue issue = getIssueIdByNumber(tGitApi, repo.getProjectIdOrPath(), number);
            List<TGitNote> issueNotes = tGitApi.getNotesApi()
                    .getIssueNotes(repo.getProjectIdOrPath(), issue.getId(), opts.getPage(), opts.getPageSize());
            return issueNotes.stream()
                    .map(TGitObjectConverter::convertComment)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public Comment createComment(ScmProviderRepository repository, Integer number, CommentInput input) {
        if (StringUtils.isBlank(input.getBody())) {
            throw new IllegalArgumentException("comment body cannot be empty");
        }

        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitIssue issue = getIssueIdByNumber(tGitApi, repo.getProjectIdOrPath(), number);
            TGitNote issueNote = tGitApi.getNotesApi()
                    .createIssueNote(repo.getProjectIdOrPath(), issue.getId(), input.getBody());
            return TGitObjectConverter.convertComment(issueNote);
        });
    }

    @Override
    public void deleteComment(ScmProviderRepository repository, Integer number, Long commentId) {

    }

    private TGitIssue getIssueIdByNumber(TGitApi tGitApi, Object projectIdOrPath, Integer number) {
        TGitIssue issue = tGitApi.getIssuesApi().getIssueByIid(projectIdOrPath, number);
        if (issue == null) {
            throw new ScmApiException(String.format("issue (%d) not found", number));
        }
        return issue;
    }

    private TGitIssueState convertFromState(IssueState from) {
        TGitIssueState tGitIssueState;
        switch (from) {
            case OPENED:
                tGitIssueState = TGitIssueState.OPENED;
                break;
            case CLOSED:
                tGitIssueState = TGitIssueState.CLOSED;
                break;
            case REOPENED:
                tGitIssueState = TGitIssueState.REOPENED;
                break;
            default:
                return null;
        }
        return tGitIssueState;
    }
}
