package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMember;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProject;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProjectHook;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitTGitProjectApiTest extends AbstractTGitTest {

    private static final Long TEST_PROJECT_HOOK_ID = 11949L;
    private static TGitApi tGitApi;
    private static TGitProjectApi projectApi;

    @BeforeAll
    public static void setup() {
        tGitApi = mockTGitApi();
        when(tGitApi.getProjectApi()).thenReturn(Mockito.mock(TGitProjectApi.class));
        projectApi = tGitApi.getProjectApi();

        when(projectApi.getProject(TEST_PROJECT_NAME))
                .thenReturn(read("get_project.json", TGitProject.class));
        when(projectApi.getProjects(1, 20))
                .thenReturn(read("list_project.json", new TypeReference<List<TGitProject>>() {
                }));
        when(projectApi.getHooks(TEST_PROJECT_NAME))
                .thenReturn(read("list_hooks.json", new TypeReference<List<TGitProjectHook>>() {
                }));
        when(projectApi.getHook(TEST_PROJECT_NAME, TEST_PROJECT_HOOK_ID))
                .thenReturn(read("get_hook.json", TGitProjectHook.class));
        when(projectApi.getProjectMembers(TEST_PROJECT_NAME, null, 1, 100))
                .thenReturn(read("get_members.json", new TypeReference<List<TGitMember>>() {
                }));
    }

    @Test
    public void getProjects() {
        List<TGitProject> projects = projectApi.getProjects(1, 20);
        Assertions.assertEquals(2, projects.size());
    }

    @Test
    public void getProject() {
        TGitProject project = projectApi.getProject(TEST_PROJECT_NAME);
        Assertions.assertEquals(130762, project.getId());
        Assertions.assertEquals("", project.getDescription());
        Assertions.assertFalse(project.getPublic());
        Assertions.assertEquals(0, project.getVisibilityLevel());
        Assertions.assertEquals(100, project.getPublicVisibility());
        Assertions.assertEquals("webhook_test3", project.getName());
        Assertions.assertEquals("webhook_test3", project.getPath());
        Assertions.assertEquals("mingshewhe/webhook_test3", project.getNameWithNamespace());
        Assertions.assertEquals("mingshewhe/webhook_test3", project.getPathWithNamespace());
        Assertions.assertEquals("master", project.getDefaultBranch());
        Assertions.assertEquals(
                "git@git.code.tencent.com:mingshewhe/webhook_test3.git",
                project.getSshUrlToRepo()
        );
        Assertions.assertEquals(
                "http://git.code.tencent.com/mingshewhe/webhook_test3.git",
                project.getHttpUrlToRepo()
        );
        Assertions.assertEquals(
                "https://git.code.tencent.com/mingshewhe/webhook_test3.git",
                project.getHttpsUrlToRepo()
        );
        Assertions.assertEquals(
                "https://git.code.tencent.com/mingshewhe/webhook_test3",
                project.getWebUrl()
        );
    }

    @Test
    public void testGetHooks() {
        List<TGitProjectHook> hooks = projectApi.getHooks(TEST_PROJECT_NAME);

        Assertions.assertEquals(3, hooks.size());
        Assertions.assertEquals(130762, hooks.get(0).getProjectId());
        Assertions.assertTrue(hooks.get(0).getMergeRequestsEvents());
        Assertions.assertTrue(hooks.get(1).getPushEvents());
        Assertions.assertTrue(hooks.get(2).getTagPushEvents());
    }

    @Test
    public void testGetHook() {
        TGitProjectHook hook = projectApi.getHook(TEST_PROJECT_NAME, TEST_PROJECT_HOOK_ID);

        Assertions.assertEquals(11949, hook.getId());
        Assertions.assertEquals(
                "https://devops.example.com/process/api/external/scm/codetgit/commit",
                hook.getUrl()
        );
        Assertions.assertEquals(130762, hook.getProjectId());
        Assertions.assertTrue(hook.getMergeRequestsEvents());
        Assertions.assertFalse(hook.getPushEvents());
    }

    @Test
    public void testGetProjectMembers() {
        List<TGitMember> members = projectApi.getProjectMembers(TEST_PROJECT_NAME, null, 1, 100);
        Assertions.assertEquals(1, members.size());
        Assertions.assertEquals("mingshewhe", members.get(0).getUsername());
    }
}
