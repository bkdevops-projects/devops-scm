package com.tencent.devops.scm.provider.git.gitee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory;
import com.tencent.devops.scm.sdk.gitee.GiteeBranchesApi;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GiteeRefServiceTest extends AbstractGiteeServiceTest {

    private static GiteeRefService refService;

    protected GiteeRefServiceTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        mockData();
    }

    public static void mockData() {
        giteeApiFactory = Mockito.mock(GiteeApiFactory.class);
        refService = new GiteeRefService(giteeApiFactory);
        providerRepository = createProviderRepository();
        GiteeApi giteeApi = Mockito.mock(GiteeApi.class);
        when(giteeApiFactory.fromAuthProvider(any()))
                .thenReturn(giteeApi);
        when(giteeApi.getBranchesApi()).thenReturn(Mockito.mock(GiteeBranchesApi.class));
        when(giteeApi.getBranchesApi().getBranches(any()))
                .thenReturn(
                        read("get_branch.json", new TypeReference<List<GiteeBranch>>() {
                        })
                );
    }

    @Test
    public void testListBranches() {
        List<Reference> references = refService.listBranches(providerRepository, null);
        references.forEach(System.out::println);
    }
}
