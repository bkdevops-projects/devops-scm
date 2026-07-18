package com.tencent.devops.scm.sdk.gitlab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tencent.devops.scm.sdk.common.exception.ScmHttpRetryException;
import com.tencent.devops.scm.sdk.gitlab.auth.GitlabTokenAuthProvider;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabIssueParams;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMergeRequest;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMergeRequestParams;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

class GitlabEndpointTest {
    @Test
    void reusesApiModules() {
        GitlabApi api = api(new TestConnector());

        assertSame(api.getProjectsApi(), api.getProjectsApi());
        assertSame(api.getProjectHooksApi(), api.getProjectHooksApi());
        assertSame(api.getBranchesApi(), api.getBranchesApi());
        assertSame(api.getTagsApi(), api.getTagsApi());
        assertSame(api.getCommitsApi(), api.getCommitsApi());
        assertSame(api.getMergeRequestsApi(), api.getMergeRequestsApi());
        assertSame(api.getIssuesApi(), api.getIssuesApi());
        assertSame(api.getNotesApi(), api.getNotesApi());
        assertSame(api.getRepositoryFilesApi(), api.getRepositoryFilesApi());
        assertSame(api.getUsersApi(), api.getUsersApi());
        assertSame(api.getCommitStatusesApi(), api.getCommitStatusesApi());
    }

    @Test
    void encodesRawProjectRefAndFilePath() {
        TestConnector connector = new TestConnector().reply(200, "{\"name\":\"feature/a\"}")
                .reply(200, "{\"file_path\":\"dir/a b.txt\"}");
        GitlabApi api = api(connector);

        api.getBranchesApi().getBranch("group/sub/project", "feature/a");
        api.getRepositoryFilesApi().getFile("group/sub/project", "dir/a b.txt", "feature/a");

        assertEquals("/api/v4/projects/group%2Fsub%2Fproject/repository/branches/feature%2Fa",
                connector.requests().get(0).url().getPath());
        assertEquals("/api/v4/projects/group%2Fsub%2Fproject/repository/files/dir%2Fa%20b%2Etxt",
                connector.requests().get(1).url().getPath());
    }

    @Test
    void mergeRequestsAndIssuesUseProjectIidPaths() {
        TestConnector connector = new TestConnector().reply(200, "{\"iid\":7}").reply(200, "{\"iid\":9}");
        GitlabApi api = api(connector);

        api.getMergeRequestsApi().updateMergeRequest(42, 7,
                GitlabMergeRequestParams.builder().title("title").build());
        api.getIssuesApi().updateIssue(42, 9, GitlabIssueParams.builder().title("issue").build());

        assertEquals("/api/v4/projects/42/merge_requests/7", connector.requests().get(0).url().getPath());
        assertEquals("/api/v4/projects/42/issues/9", connector.requests().get(1).url().getPath());
    }

    @Test
    void rejectsPerPageAboveGitlabLimit() {
        GitlabApi api = api(new TestConnector());
        assertThrows(IllegalArgumentException.class,
                () -> api.getBranchesApi().getBranches(1, null, 1, 101));
    }

    @Test
    void omitsTextEncodingAndSendsExplicitBase64Encoding() throws IOException {
        TestConnector connector = new TestConnector().reply(200, "{}").reply(200, "{}").reply(200, "{}");
        GitlabRepositoryFilesApi filesApi = api(connector).getRepositoryFilesApi();

        filesApi.updateFile(1, ".ci/pipeline.yml", "main", "content", "update");
        filesApi.createFile(1, "legacy.txt", "main", "text", "content", "create");
        filesApi.updateFile(1, "binary.dat", "main", "base64", "YmluYXJ5", "binary");

        String defaultBody = IOUtils.toString(connector.requests().get(0).body(), StandardCharsets.UTF_8);
        String legacyBody = IOUtils.toString(connector.requests().get(1).body(), StandardCharsets.UTF_8);
        String base64Body = IOUtils.toString(connector.requests().get(2).body(), StandardCharsets.UTF_8);
        assertFalse(defaultBody.contains("\"encoding\""));
        assertFalse(legacyBody.contains("\"encoding\""));
        assertTrue(base64Body.contains("\"encoding\":\"base64\""));
    }

    @Test
    void mergeRequestChangesExposeOverflowAndAcceptLocalDateMilestones() {
        TestConnector connector = new TestConnector().reply(200,
                "{\"iid\":7,\"overflow\":true,\"changes\":[],"
                        + "\"reviewers\":[{\"username\":\"reviewer\"}],"
                        + "\"diff_refs\":{\"base_sha\":\"base\",\"start_sha\":\"target\",\"head_sha\":\"source\"},"
                        + "\"milestone\":{\"id\":1,\"due_date\":\"2026-07-31\"}}");

        GitlabMergeRequest mergeRequest = api(connector).getMergeRequestsApi().getMergeRequestChanges(1, 7);

        assertEquals(Boolean.TRUE, mergeRequest.getOverflow());
        assertEquals("reviewer", mergeRequest.getReviewers().get(0).getUsername());
        assertEquals("base", mergeRequest.getDiffRefs().getBaseSha());
        assertEquals("target", mergeRequest.getDiffRefs().getStartSha());
        assertEquals("source", mergeRequest.getDiffRefs().getHeadSha());
        assertEquals(LocalDate.of(2026, 7, 31), mergeRequest.getMilestone().getDueDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate());
    }

    @Test
    void retriesConflictOnlyWhenCreatingCommitStatus() {
        RuntimeException statusError = assertThrows(RuntimeException.class,
                () -> api(new TestConnector().reply(409, "{\"message\":\"conflict\"}"))
                        .getCommitStatusesApi().create(1, "abc", "success", "build", "main", null, null));
        RuntimeException otherError = assertThrows(RuntimeException.class,
                () -> api(new TestConnector().reply(409, "{\"message\":\"conflict\"}"))
                        .getUsersApi().getCurrentUser());

        assertInstanceOf(ScmHttpRetryException.class, statusError);
        assertInstanceOf(GitlabApiException.class, otherError);
    }

    private GitlabApi api(TestConnector connector) {
        return new GitlabApi("https://gitlab.example/api/v4", connector,
                GitlabTokenAuthProvider.privateToken("token"));
    }
}
