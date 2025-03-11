package com.tencent.devops.scm.provider.git.tgit;


import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.api.enums.Visibility;
import com.tencent.devops.scm.api.pojo.Hook;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.RepoListOptions;
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.sdk.tgit.TGitProjectApi;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProject;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProjectHook;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitRepositoryServiceTest extends AbstractTGitServiceTest {
    private static final Long TEST_PROJECT_HOOK_ID = 11949L;

    private static TGitRepositoryService repositoryService;

    public TGitRepositoryServiceTest() {
        super();
        when(tGitApi.getProjectApi()).thenReturn(Mockito.mock(TGitProjectApi.class));

        TGitProjectApi projectApi = tGitApi.getProjectApi();
        when(projectApi.getProject(TEST_PROJECT_NAME))
                .thenReturn(read("get_project.json", TGitProject.class));
        when(projectApi.getProjects("webhook_test3", null, null))
                .thenReturn(read("list_project.json", new TypeReference<List<TGitProject>>() {
                }));
        when(projectApi.getHooks(TEST_PROJECT_NAME, null, null))
                .thenReturn(read("list_hooks.json", new TypeReference<List<TGitProjectHook>>() {
                }));
        when(projectApi.getHook(TEST_PROJECT_NAME, TEST_PROJECT_HOOK_ID))
                .thenReturn(read("get_hook.json", TGitProjectHook.class));

        repositoryService = new TGitRepositoryService(apiFactory);
    }

    @Test
    public void testFind() {
        GitScmServerRepository serverRepository = repositoryService.find(providerRepository);

        Assertions.assertEquals(130762L, serverRepository.getId());
        Assertions.assertTrue(serverRepository.getPrivate());
        Assertions.assertFalse(serverRepository.getArchived());
        Assertions.assertEquals(Visibility.PRIVATE, serverRepository.getVisibility());
        Assertions.assertEquals("webhook_test3", serverRepository.getName());
        Assertions.assertEquals("mingshewhe", serverRepository.getGroup());
        Assertions.assertEquals("mingshewhe/webhook_test3", serverRepository.getFullName());
        Assertions.assertEquals("master", serverRepository.getDefaultBranch());
        Assertions.assertEquals(
                "git@git.code.tencent.com:mingshewhe/webhook_test3.git",
                serverRepository.getSshUrl()
        );
        Assertions.assertEquals(
                "https://git.code.tencent.com/mingshewhe/webhook_test3.git",
                serverRepository.getHttpUrl()
        );
        Assertions.assertEquals(
                "https://git.code.tencent.com/mingshewhe/webhook_test3",
                serverRepository.getWebUrl()
        );
    }

    @Test
    public void testList() {
        RepoListOptions opts = RepoListOptions.builder()
                .repoName("webhook_test3")
                .build();
        List<ScmServerRepository> serverRepositories = repositoryService.list(providerRepository.getAuth(), opts);
        Assertions.assertEquals(2, serverRepositories.size());
    }

    @Test
    public void testGetHook() {
            Hook hook = repositoryService.getHook(providerRepository, TEST_PROJECT_HOOK_ID);

        Assertions.assertEquals(11949, hook.getId());
        Assertions.assertEquals(
                "https://devops.example.com/process/api/external/scm/codetgit/commit",
                hook.getUrl()
        );
        Assertions.assertTrue(hook.getEvents().getPullRequest());
    }

    @Test
    public void listHooks() {
        ListOptions opts = ListOptions.builder().build();
        List<Hook> hooks = repositoryService.listHooks(providerRepository, opts);

        Assertions.assertEquals(3, hooks.size());
    }
}
