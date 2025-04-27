package com.tencent.devops.scm.sdk.tgit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeRepositoryFileApi;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCommitCompare;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeFileChange;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GiteeFileApiTest extends AbstractGiteeTest {

    private static GiteeApi giteeApi;

    private static final String BASE_COMMIT = "851457853412a898063baed4efa2e1053745c04a";
    private static final String HEAD_COMMIT = "fae606af2642ecfbdefb10f502549533b1540702";

    public GiteeFileApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        giteeApi = createGiteeApi();
        mockData();
    }

    public static void mockData() {
        giteeApi = Mockito.mock(GiteeApi.class);
        Mockito.when(giteeApi.getFileApi()).thenReturn(Mockito.mock(GiteeRepositoryFileApi.class));
        Mockito.when(giteeApi.getFileApi().commitCompare(TEST_PROJECT_NAME, BASE_COMMIT, HEAD_COMMIT, false))
                .thenReturn(
                        read("commit_compare.json", new TypeReference<>() {})
                );
    }

    @Test
    public void testCommitCompare() throws JsonProcessingException {
        GiteeCommitCompare commitCompare = giteeApi.getFileApi().commitCompare(
                TEST_PROJECT_NAME,
                BASE_COMMIT,
                HEAD_COMMIT,
                false
        );
        Set<String> files = commitCompare.getFiles()
                .stream()
                .map(GiteeFileChange::getFilename)
                .collect(Collectors.toSet());
        String fileName = "src/backend/ci/core/project/biz-project/src/main"
                + "/kotlin/com/tencent/devops/project/service/impl/AbsUserProjectServiceServiceImpl.kt";
        Assertions.assertTrue(files.contains(fileName));
    }
}
