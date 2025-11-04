package com.tencent.devops.scm.sdk.bkcode;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeEventType;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeMember;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodePage;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeRepositoryDetail;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeUser;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeWebhookConfig;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BkCodeProjectApiTest extends AbstractBkCodeTest {

    private static final Long TEST_PROJECT_HOOK_ID = 4L;

    @BeforeAll
    public static void setup() {
        if (MOCK_DATA) {
            mock();
        } else {
            bkCodeApi = createBkCodeApi();
        }
    }

    public static void mock() {
        bkCodeApi = mockBkCodeApi();
        when(bkCodeApi.getProjectApi()).thenReturn(Mockito.mock(BkCodeProjectApi.class));
        when(bkCodeApi.getProjectApi().getProject(TEST_PROJECT_NAME))
                .thenReturn(
                        read(
                                "get_repo_detail.json",
                                new TypeReference<BkCodeResult<BkCodeRepositoryDetail>>() {}
                        ).getData()
                );
        when(bkCodeApi.getProjectApi().getProjects(TEST_PROJECT_NAME, 1, 20))
                .thenReturn(
                        read(
                                "get_repo_list.json",
                                new TypeReference<BkCodeResult<BkCodePage<BkCodeRepositoryDetail>>>() {}
                        ).getData()
                );
        when(bkCodeApi.getProjectApi().getHooks(TEST_PROJECT_NAME))
                .thenReturn(
                        read(
                                "get_webhook_list.json",
                                new TypeReference<BkCodeResult<BkCodePage<BkCodeWebhookConfig>>>() {}
                        ).getData()
                );
        when(bkCodeApi.getProjectApi().getHook(TEST_PROJECT_NAME, TEST_PROJECT_HOOK_ID))
                .thenReturn(
                        read(
                                "get_webhook_detail.json",
                                new TypeReference<BkCodeResult<BkCodeWebhookConfig>>() {}
                        ).getData()
                );
        when(bkCodeApi.getProjectApi().getAllMembers(TEST_PROJECT_NAME, null, 1, 100))
                .thenReturn(
                        read(
                                "get_repo_members.json",
                                new TypeReference<BkCodeResult<List<BkCodeMember>>>() {}
                        ).getData()
                );
    }

    @Test
    public void getProjects() {
        BkCodePage<BkCodeRepositoryDetail> projectsPage = bkCodeApi.getProjectApi()
                .getProjects(TEST_PROJECT_NAME,1, 20);
        List<BkCodeRepositoryDetail> projects = projectsPage.getRecords();
        Assertions.assertEquals(1, projects.size());
        BkCodeRepositoryDetail repoDetail0 = projects.get(0);
        Assertions.assertEquals(11, repoDetail0.getId());
        Assertions.assertEquals("hejieehe/devops_trigger", repoDetail0.getPath());
        Assertions.assertEquals(9, repoDetail0.getGroupId());

        BkCodeUser creator = repoDetail0.getCreator();
        Assertions.assertEquals("zhangsan", creator.getUsername());
    }

    @Test
    public void getProject() {
        BkCodeRepositoryDetail project = bkCodeApi.getProjectApi().getProject(TEST_PROJECT_NAME);
        Assertions.assertEquals(11, project.getId());
        Assertions.assertEquals("", project.getDesc());
        Assertions.assertEquals("devops_trigger", project.getName());
        Assertions.assertEquals("hejieehe/devops_trigger", project.getPath());
    }

    @Test
    public void testGetHooks() {
        List<BkCodeWebhookConfig> hooks = bkCodeApi.getProjectApi().getHooks(TEST_PROJECT_NAME).getRecords();

        Assertions.assertEquals(1, hooks.size());
        Assertions.assertTrue(hooks.get(0).getEvents().contains(BkCodeEventType.PUSH.getHookName()));
        Assertions.assertTrue(hooks.get(0).getEvents().contains(BkCodeEventType.MERGE_REQUEST.getHookName()));
        Assertions.assertTrue(hooks.get(0).getEvents().contains(BkCodeEventType.ISSUES.getHookName()));
    }

    @Test
    public void testGetHook() {
        BkCodeWebhookConfig hook = bkCodeApi.getProjectApi().getHook(TEST_PROJECT_NAME, TEST_PROJECT_HOOK_ID);
        Assertions.assertEquals(4, hook.getId());
        Assertions.assertEquals(
                "https://devops.example.com/process/api/external/scm/codeBkCode/commit",
                hook.getUrl()
        );
        Assertions.assertTrue(hook.getEvents().contains(BkCodeEventType.PUSH.getHookName()));
        Assertions.assertTrue(hook.getEvents().contains(BkCodeEventType.MERGE_REQUEST.getHookName()));
    }

    @Test
    public void testGetProjectMembers() {
        List<BkCodeMember> members = bkCodeApi.getProjectApi().getAllMembers(TEST_PROJECT_NAME, null, 1, 100);
        Assertions.assertEquals(1, members.size());
        Assertions.assertEquals("zhangsan", members.get(0).getUser().getUsername());
    }
}
