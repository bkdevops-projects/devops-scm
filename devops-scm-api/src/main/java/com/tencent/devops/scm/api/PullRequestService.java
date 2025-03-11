package com.tencent.devops.scm.api;

import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Comment;
import com.tencent.devops.scm.api.pojo.CommentInput;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.PullRequest;
import com.tencent.devops.scm.api.pojo.PullRequestInput;
import com.tencent.devops.scm.api.pojo.PullRequestListOptions;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import java.util.List;

public interface PullRequestService {

    PullRequest find(ScmProviderRepository repository, Integer number);

    List<PullRequest> list(ScmProviderRepository repository, PullRequestListOptions opts);

    PullRequest create(ScmProviderRepository repository, PullRequestInput input);

    List<Change> listChanges(ScmProviderRepository repository, Integer number, ListOptions opts);

    List<Commit> listCommits(ScmProviderRepository repository, Integer number, ListOptions opts);

    void merge(ScmProviderRepository repository, Integer number);

    void close(ScmProviderRepository repository, Integer number);

    Comment findComment(ScmProviderRepository repository, Integer number, Long commentId);

    List<Comment> listComments(ScmProviderRepository repository, Integer number, ListOptions opts);

    Comment createComment(ScmProviderRepository repository, Integer number, CommentInput input);

    void deleteComment(ScmProviderRepository repository, Integer number, Long commentId);
}
