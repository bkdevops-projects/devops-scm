package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabNote;
import java.util.Arrays;
import java.util.List;

public class GitlabNotesApi extends AbstractGitlabApi {
    GitlabNotesApi(GitlabApi api) {
        super(api);
    }

    public List<GitlabNote> getMergeRequestNotes(Object project, long iid, Integer page, Integer perPage) {
        return list(project, "projects/%s/merge_requests/" + iid + "/notes", page, perPage);
    }

    public GitlabNote getMergeRequestNote(Object project, long iid, long noteId) {
        return get(project, "projects/%s/merge_requests/" + iid + "/notes/" + noteId);
    }

    public GitlabNote createMergeRequestNote(Object project, long iid, String body) {
        return create(project, "projects/%s/merge_requests/" + iid + "/notes", body);
    }

    public void deleteMergeRequestNote(Object project, long iid, long noteId) {
        delete(project, "projects/%s/merge_requests/" + iid + "/notes/" + noteId);
    }

    public List<GitlabNote> getIssueNotes(Object project, long iid, Integer page, Integer perPage) {
        return list(project, "projects/%s/issues/" + iid + "/notes", page, perPage);
    }

    public GitlabNote getIssueNote(Object project, long iid, long noteId) {
        return get(project, "projects/%s/issues/" + iid + "/notes/" + noteId);
    }

    public GitlabNote createIssueNote(Object project, long iid, String body) {
        return create(project, "projects/%s/issues/" + iid + "/notes", body);
    }

    public void deleteIssueNote(Object project, long iid, long noteId) {
        delete(project, "projects/%s/issues/" + iid + "/notes/" + noteId);
    }

    private List<GitlabNote> list(Object project, String path, Integer page, Integer perPage) {
        GitlabNote[] result = request(project, path).method(ScmHttpMethod.GET).with(PAGE_PARAM, page(page))
                .with(PER_PAGE_PARAM, perPage(perPage)).fetch(GitlabNote[].class);
        return Arrays.asList(result);
    }

    private GitlabNote get(Object project, String path) {
        return request(project, path).method(ScmHttpMethod.GET).fetch(GitlabNote.class);
    }

    private GitlabNote create(Object project, String path, String body) {
        return request(project, path).method(ScmHttpMethod.POST).with("body", body).fetch(GitlabNote.class);
    }

    private void delete(Object project, String path) {
        request(project, path).method(ScmHttpMethod.DELETE).send();
    }
}
