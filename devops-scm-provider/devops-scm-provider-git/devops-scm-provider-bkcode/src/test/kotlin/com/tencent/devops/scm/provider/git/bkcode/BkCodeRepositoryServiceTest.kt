package com.tencent.devops.scm.provider.git.bkcode

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.pojo.HookEvents
import com.tencent.devops.scm.api.pojo.HookInput
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.sdk.bkcode.BkCodeConstants.DEFAULT_PAGE
import com.tencent.devops.scm.sdk.bkcode.BkCodeConstants.DEFAULT_PER_PAGE
import com.tencent.devops.scm.sdk.bkcode.BkCodeProjectApi
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodePage
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeProjectHookInput
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeRepositoryDetail
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeWebhookConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class BkCodeRepositoryServiceTest : AbstractBkCodeServiceTest() {

    companion object {
        private const val TEST_PROJECT_HOOK_ID = 4L
    }

    private lateinit var repositoryService: BkCodeRepositoryService

    private val hookInput = HookInput(
        name = "bk_ci_devops_trigger_dev",
        url = "https://dev-api.bkdevops.qq.com/process/api/external/scm/bkCode/commit\n",
        events = HookEvents(pullRequestReview = true)
    )

    private val hookConfig = BkCodeObjectConverter.convertFromHookInput(hookInput)

    init {
        apiFactory = createBkCodeApiFactory()
        repositoryService = BkCodeRepositoryService(apiFactory)
        mockData()
    }

    private fun mockData() {
        val projectApi: BkCodeProjectApi = mock()
        apiFactory = mockBkCodeApiFactory()
        repositoryService = BkCodeRepositoryService(apiFactory)
        `when`(apiFactory.fromAuthProvider(any())).thenReturn(bkCodeApi)
        `when`(bkCodeApi.projectApi).thenReturn(projectApi)
        `when`(
            projectApi.getProject(any())
        ).thenReturn(
            read(
                "get_repo_detail.json",
                object : TypeReference<BkCodeResult<BkCodeRepositoryDetail>>() {}
            ).data
        )
        `when`(
            projectApi.getProjects(
                TEST_PROJECT_NAME,
                DEFAULT_PAGE,
                DEFAULT_PER_PAGE
            )
        ).thenReturn(
            read(
                "get_repo_list.json",
                object : TypeReference<BkCodeResult<BkCodePage<BkCodeRepositoryDetail>>>() {}
            ).data
        )
        `when`(
            projectApi.getHooks(TEST_PROJECT_NAME)
        ).thenReturn(
            read(
                "get_webhook_list.json",
                object : TypeReference<BkCodeResult<BkCodePage<BkCodeWebhookConfig>>>() {}
            ).data
        )
        `when`(
            projectApi.getHooks(
                TEST_PROJECT_NAME,
                DEFAULT_PAGE,
                DEFAULT_PER_PAGE
            )
        ).thenReturn(
            read(
                "get_webhook_list.json",
                object : TypeReference<BkCodeResult<BkCodePage<BkCodeWebhookConfig>>>() {}
            ).data
        )

        `when`(
            projectApi.getHook(
                TEST_PROJECT_NAME,
                TEST_PROJECT_HOOK_ID
            )
        ).thenReturn(
            read(
                "get_webhook_detail.json",
                object : TypeReference<BkCodeResult<BkCodeWebhookConfig>>() {}
            ).data
        )

        `when`(
            projectApi.addHook(
                TEST_PROJECT_NAME,
                hookConfig
            )
        ).thenReturn(
            read(
                "get_webhook_detail.json",
                object : TypeReference<BkCodeResult<BkCodeWebhookConfig>>() {}
            ).data
        )

        `when`(
            projectApi.updateHook(
                TEST_PROJECT_NAME,
                TEST_PROJECT_HOOK_ID,
                hookConfig
            )
        ).thenReturn(
            read(
                "get_webhook_detail.json",
                object : TypeReference<BkCodeResult<BkCodeWebhookConfig>>() {}
            ).data
        )
    }

    @Test
    fun testGetHook() {
        val hook = repositoryService.getHook(providerRepository, TEST_PROJECT_HOOK_ID)

        Assertions.assertEquals(4, hook.id)
        Assertions.assertEquals(
            "https://devops.example.com/process/api/external/scm/codeBkCode/commit",
            hook.url
        )
        Assertions.assertTrue(hook.events?.pullRequest ?: false)
    }

    @Test
    fun listHooks() {
        val hooks = repositoryService.listHooks(
            providerRepository,
            ListOptions(
                page = DEFAULT_PAGE,
                pageSize = DEFAULT_PER_PAGE
            )
        )
        Assertions.assertEquals(1, hooks.size)
    }

    @Test
    fun addHook() {
        val hooks = repositoryService.createHook(
            providerRepository,
            hookInput
        )
        println("hooks = $hooks")
    }
}
