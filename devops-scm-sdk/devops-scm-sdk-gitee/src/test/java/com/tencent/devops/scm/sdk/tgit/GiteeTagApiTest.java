package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.ArgumentMatchers.anyString;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeTagApi;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeTag;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeTagDetail;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GiteeTagApiTest extends AbstractGiteeTest {

    private static GiteeApi giteeApi;

    public GiteeTagApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        giteeApi = createGiteeApi();
        mockData();
    }

    public static void mockData() {
        giteeApi = Mockito.mock(GiteeApi.class);
        Mockito.when(giteeApi.getTagApi()).thenReturn(Mockito.mock(GiteeTagApi.class));
        Mockito.when(giteeApi.getTagApi().getTags(TEST_PROJECT_NAME))
                .thenReturn(
                        read("get_tag_list.json", new TypeReference<>() {
                        })
                );
        Mockito.when(giteeApi.getTagApi().getTags(anyString(), anyString()))
                .thenReturn(
                        read("get_tag_detail.json", new TypeReference<>() {
                        })
                );
    }

    @Test
    public void testGetTags() {
        List<GiteeTag> tags = giteeApi.getTagApi().getTags(TEST_PROJECT_NAME);
        tags.forEach(System.out::println);
    }

    @Test
    public void testGetTag() {
        GiteeTagDetail tags = giteeApi.getTagApi().getTags(TEST_PROJECT_NAME, "v1.0.0");
        System.out.println(tags);
    }
}
