package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.UriTemplate;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitNote;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitReviewNoteParams;
import java.util.Arrays;
import java.util.List;

/**
 * 评论 接口
 * <pre>接口文档: <a href="https://code.tencent.com/help/api/comment">工蜂评论接口文档</a></pre>
 */
public class TGitNotesApi extends AbstractTGitApi {
    private static final String PROJECT_ID_ISSUES_ID_NOTE_URI_PATTERN =
            "projects/:id/issues/:issue_id/notes";
    private static final String PROJECT_ID_ISSUES_ID_NOTE_ID_URI_PATTERN =
            "projects/:id/issues/:issue_id/notes/:note_id";
    private static final String PROJECT_ID_MR_ID_NOTE_URI_PATTERN =
            "projects/:id/merge_requests/:merge_request_id/notes";
    private static final String PROJECT_ID_MR_ID_NOTE_ID_URI_PATTERN =
            "projects/:id/merge_requests/:merge_request_id/notes/:note_id";
    private static final String PROJECT_ID_CR_ID_NOTE_URI_PATTERN =
            "projects/:id/reviews/:review_id/notes";
    private static final String PROJECT_ID_CR_ID_NOTE_ID_URI_PATTERN =
            "projects/:id/reviews/:review_id/notes/:note_id";

    public TGitNotesApi(TGitApi tGitApi) {
        super(tGitApi);
    }

    public TGitNote createIssueNote(Object projectIdOrPath, Long issueId, String body) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        PROJECT_ID_ISSUES_ID_NOTE_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("body", repoId)
                                .add("issue_id", issueId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .with("body", body)
                .fetch(TGitNote.class);
    }

    public TGitNote updateIssueNote(Object projectIdOrPath, Long issueId, Long noteId, String body) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        PROJECT_ID_ISSUES_ID_NOTE_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("issue_id", issueId.toString())
                                .add("note_id", noteId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .with("body", body)
                .fetch(TGitNote.class);
    }

    public TGitNote getIssueNote(Object projectIdOrPath, Long issueId, Long noteId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_ID_ISSUES_ID_NOTE_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("issue_id", issueId.toString())
                                .add("note_id", noteId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TGitNote.class);
    }

    public List<TGitNote> getIssueNotes(Object projectIdOrPath, Long issueId) {
        return getIssueNotes(projectIdOrPath, issueId, DEFAULT_PAGE, DEFAULT_PER_PAGE);
    }

    public List<TGitNote> getIssueNotes(Object projectIdOrPath, Long issueId, Integer page, Integer perPage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitNote[] notes = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_ID_ISSUES_ID_NOTE_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("issue_id", issueId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(TGitNote[].class);
        return Arrays.asList(notes);
    }

    public TGitNote createMergeRequestNote(Object projectIdOrPath, Long mergeRequestId, String body) {
        TGitReviewNoteParams params = TGitReviewNoteParams.builder().body(body).build();
        return createMergeRequestNote(projectIdOrPath, mergeRequestId, params);
    }

    public TGitNote createMergeRequestNote(Object projectIdOrPath, Long mergeRequestId, TGitReviewNoteParams params) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        PROJECT_ID_MR_ID_NOTE_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("merge_request_id", mergeRequestId.toString())
                                .build()
                )
                .withRepoId(repoId);
        fillBody(requester, params, true);
        return requester.fetch(TGitNote.class);
    }

    public TGitNote updateMergeRequestNote(Object projectIdOrPath, Long mergeRequestId, Long noteId, String body) {
        TGitReviewNoteParams params = TGitReviewNoteParams.builder().body(body).build();
        return updateMergeRequestNote(projectIdOrPath, mergeRequestId, noteId, params);
    }

    public TGitNote updateMergeRequestNote(Object projectIdOrPath, Long mergeRequestId, Long noteId,
            TGitReviewNoteParams params) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        PROJECT_ID_MR_ID_NOTE_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("merge_request_id", mergeRequestId.toString())
                                .add("note_id", noteId.toString())
                                .build()
                )
                .withRepoId(repoId);
        fillBody(requester, params, false);
        return requester.fetch(TGitNote.class);
    }

    public List<TGitNote> getMergeRequestNotes(Object projectIdOrPath, Long mergeRequestId) {
        return getMergeRequestNotes(projectIdOrPath, mergeRequestId, DEFAULT_PAGE, DEFAULT_PER_PAGE);
    }

    public List<TGitNote> getMergeRequestNotes(Object projectIdOrPath, Long mergeRequestId,
            Integer page, Integer perPage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitNote[] notes = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_ID_MR_ID_NOTE_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("merge_request_id", mergeRequestId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(TGitNote[].class);
        return Arrays.asList(notes);
    }

    public TGitNote getMergeRequestNote(Object projectIdOrPath, Long mergeRequestId, Long noteId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_ID_MR_ID_NOTE_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("merge_request_id", mergeRequestId.toString())
                                .add("note_id", noteId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TGitNote.class);
    }

    public TGitNote createReviewNote(Object projectIdOrPath, Long mergeRequestId, String body) {
        TGitReviewNoteParams params = TGitReviewNoteParams.builder().body(body).build();
        return createReviewNote(projectIdOrPath, mergeRequestId, params);
    }

    public TGitNote createReviewNote(Object projectIdOrPath, Long reviewId, TGitReviewNoteParams params) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        PROJECT_ID_CR_ID_NOTE_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("review_id", reviewId.toString())
                                .build()
                )
                .withRepoId(repoId);
        fillBody(requester, params, true);
        return requester.fetch(TGitNote.class);
    }

    public TGitNote updateReviewNote(Object projectIdOrPath, Long mergeRequestId, Long noteId, String body) {
        TGitReviewNoteParams params = TGitReviewNoteParams.builder().body(body).build();
        return updateMergeRequestNote(projectIdOrPath, mergeRequestId, noteId, params);
    }

    public TGitNote updateReviewNote(Object projectIdOrPath, Long reviewId, Long noteId,
            TGitReviewNoteParams params) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        PROJECT_ID_CR_ID_NOTE_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("review_id", reviewId.toString())
                                .add("note_id", noteId.toString())
                                .build()
                )
                .withRepoId(repoId);
        fillBody(requester, params, false);
        return requester.fetch(TGitNote.class);
    }

    public List<TGitNote> getReviewNotes(Object projectIdOrPath, Long mergeRequestId) {
        return getReviewNotes(projectIdOrPath, mergeRequestId, DEFAULT_PAGE, DEFAULT_PER_PAGE);
    }

    public List<TGitNote> getReviewNotes(Object projectIdOrPath, Long reviewId, Integer page, Integer perPage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitNote[] notes = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_ID_CR_ID_NOTE_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("review_id", reviewId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(TGitNote[].class);
        return Arrays.asList(notes);
    }

    public TGitNote getReviewsNote(Object projectIdOrPath, Long reviewId, Long noteId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_ID_CR_ID_NOTE_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("review_id", reviewId.toString())
                                .add("note_id", noteId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TGitNote.class);
    }

    private void fillBody(Requester requester, TGitReviewNoteParams params, boolean isCreate) {
        requester.with("body", params.getBody())
                .with("reviewer_state",
                        params.getReviewerState() == null ? null : params.getReviewerState().toValue()
                );
        if (isCreate) {
            requester.with("line", params.getLine())
                    .with("path", params.getPath())
                    .with("line_type", params.getLineType() == null ? null : params.getLineType().toValue());
        }
    }
}
