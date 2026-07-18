package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabRepositoryFile;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabTreeItem;
import java.util.Arrays;
import java.util.List;

public class GitlabRepositoryFilesApi extends AbstractGitlabApi {
    GitlabRepositoryFilesApi(GitlabApi api) {
        super(api);
    }

    public GitlabRepositoryFile getFile(Object project, String filePath, String ref) {
        requireRef(ref);
        return request(project, "projects/%s/repository/files/" + segment(filePath)).method(ScmHttpMethod.GET)
                .with("ref", ref).fetch(GitlabRepositoryFile.class);
    }

    public void createFile(Object project, String filePath, String branch, String encoding, String content,
            String commitMessage) {
        write(request(project, "projects/%s/repository/files/" + segment(filePath)).method(ScmHttpMethod.POST),
                branch, encoding, content, commitMessage).send();
    }

    public void createFile(Object project, String filePath, String branch, String content, String commitMessage) {
        createFile(project, filePath, branch, null, content, commitMessage);
    }

    public void updateFile(Object project, String filePath, String branch, String encoding, String content,
            String commitMessage) {
        write(request(project, "projects/%s/repository/files/" + segment(filePath)).method(ScmHttpMethod.PUT),
                branch, encoding, content, commitMessage).send();
    }

    public void updateFile(Object project, String filePath, String branch, String content, String commitMessage) {
        updateFile(project, filePath, branch, null, content, commitMessage);
    }

    public void deleteFile(Object project, String filePath, String branch, String commitMessage) {
        request(project, "projects/%s/repository/files/" + segment(filePath)).method(ScmHttpMethod.DELETE)
                .with("branch", branch).with("commit_message", commitMessage).send();
    }

    public List<GitlabTreeItem> getTree(Object project, String path, String ref, Boolean recursive,
            Integer page, Integer perPage) {
        requireRef(ref);
        GitlabTreeItem[] result = request(project, "projects/%s/repository/tree").method(ScmHttpMethod.GET)
                .with("path", path).with("ref", ref).with("recursive", recursive)
                .with(PAGE_PARAM, page(page)).with(PER_PAGE_PARAM, perPage(perPage)).fetch(GitlabTreeItem[].class);
        return Arrays.asList(result);
    }

    private Requester write(Requester request, String branch, String encoding, String content, String message) {
        request.with("branch", branch).with("content", content).with("commit_message", message);
        if (encoding != null && !"text".equalsIgnoreCase(encoding)) {
            request.with("encoding", encoding);
        }
        return request;
    }

    private void requireRef(String ref) {
        if (ref == null || ref.trim().isEmpty()) {
            throw new IllegalArgumentException("ref cannot be blank");
        }
    }
}
