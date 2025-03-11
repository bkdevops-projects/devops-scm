package com.tencent.devops.scm.api;

import com.tencent.devops.scm.api.pojo.Comment;
import com.tencent.devops.scm.api.pojo.CommentInput;
import com.tencent.devops.scm.api.pojo.Issue;
import com.tencent.devops.scm.api.pojo.IssueInput;
import com.tencent.devops.scm.api.pojo.IssueListOptions;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import java.util.List;

public interface IssueService {

    Issue find(ScmProviderRepository repository, Integer number);

    Issue create(ScmProviderRepository repository, IssueInput input);

    List<Issue> list(ScmProviderRepository repository, IssueListOptions opts);

    void close(ScmProviderRepository repository, Integer number);

    Comment findComment(ScmProviderRepository repository, Integer number, Long commentId);

    List<Comment> listComments(ScmProviderRepository repository, Integer number, ListOptions opts);

    Comment createComment(ScmProviderRepository repository, Integer number, CommentInput input);

    void deleteComment(ScmProviderRepository repository, Integer number, Long commentId);
}
