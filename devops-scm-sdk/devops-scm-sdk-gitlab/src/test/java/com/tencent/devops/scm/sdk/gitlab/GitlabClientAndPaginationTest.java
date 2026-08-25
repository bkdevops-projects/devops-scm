package com.tencent.devops.scm.sdk.gitlab;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tencent.devops.scm.sdk.common.exception.ScmHttpRetryException;
import com.tencent.devops.scm.sdk.gitlab.auth.GitlabTokenAuthProvider;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMember;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabProject;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class GitlabClientAndPaginationTest {
    @Test
    void followsNextPageHeaderAndStopsWhenItIsEmpty() {
        TestConnector connector = new TestConnector()
                .reply(200, "[{\"id\":1}]", TestConnector.headers(
                        "X-Page", "1", "X-Total-Pages", "3", "X-Next-Page", "2"))
                .reply(200, "[{\"id\":2}]", TestConnector.headers(
                        "X-Page", "2", "X-Total-Pages", "3", "X-Next-Page", ""));
        List<Long> ids = new ArrayList<>();

        for (GitlabProject project : api(connector).getProjectsApi().getProjects(1)) {
            ids.add(project.getId());
        }

        assertEquals(List.of(1L, 2L), ids);
        assertEquals("1", query(connector.requests().get(0).url().getQuery(), "page"));
        assertEquals("2", query(connector.requests().get(1).url().getQuery(), "page"));
    }

    @Test
    void convertsJsonClientErrorsAndPreservesRequestId() {
        TestConnector connector = new TestConnector().reply(403, "{\"message\":\"forbidden\"}",
                TestConnector.headers("Content-Type", "application/json", "X-Request-Id", "request-123"));

        GitlabApiException error = assertThrows(GitlabApiException.class,
                () -> api(connector).getUsersApi().getCurrentUser());

        assertEquals(403, error.getStatusCode());
        assertEquals("forbidden", error.getMessage());
        assertEquals("request-123", error.getRequestId());
    }

    @Test
    void extractsGitlabOauthErrorMessages() {
        GitlabApiException description = assertThrows(GitlabApiException.class,
                () -> api(new TestConnector().reply(400,
                        "{\"error\":\"invalid_grant\",\"error_description\":\"Code expired\"}"))
                        .getUsersApi().getCurrentUser());
        GitlabApiException error = assertThrows(GitlabApiException.class,
                () -> api(new TestConnector().reply(400, "{\"error\":\"invalid_grant\"}"))
                        .getUsersApi().getCurrentUser());

        assertEquals("Code expired", description.getMessage());
        assertEquals("invalid_grant", error.getMessage());
    }

    @Test
    void getMemberFollowsPaginationAndMatchesExactUsername() {
        TestConnector connector = new TestConnector()
                .reply(200, "[{\"id\":1,\"username\":\"target-user-old\"}]",
                        TestConnector.headers("X-Next-Page", "2"))
                .reply(200, "[{\"id\":2,\"username\":\"target-user\"}]",
                        TestConnector.headers("X-Next-Page", ""));

        GitlabMember member = api(connector).getProjectsApi().getMember(1, "target-user");

        assertEquals(2L, member.getId());
        assertEquals("2", query(connector.requests().get(1).url().getQuery(), "page"));
    }

    @Test
    void marksRateLimitAndRetryableServerStatusesForRetry() {
        for (int status : new int[]{429, 500, 502, 503, 504}) {
            RuntimeException error = assertThrows(RuntimeException.class,
                    () -> api(new TestConnector().reply(status, "{}")).getUsersApi().getCurrentUser());
            assertInstanceOf(ScmHttpRetryException.class, error);
        }
    }

    @Test
    void acceptsNoContentResponsesWithoutDeserialization() {
        TestConnector connector = new TestConnector().reply(204, "").reply(204, "");
        assertDoesNotThrow(() -> api(connector).getBranchesApi().deleteBranch(1, "obsolete"));
        assertNull(api(connector).getUsersApi().getCurrentUser());
    }

    private GitlabApi api(TestConnector connector) {
        return new GitlabApi("https://gitlab.example/api/v4", connector,
                GitlabTokenAuthProvider.privateToken("token"));
    }

    private String query(String query, String key) {
        for (String item : query.split("&")) {
            String[] pair = item.split("=", 2);
            if (pair[0].equals(key)) {
                return pair[1];
            }
        }
        return null;
    }
}
