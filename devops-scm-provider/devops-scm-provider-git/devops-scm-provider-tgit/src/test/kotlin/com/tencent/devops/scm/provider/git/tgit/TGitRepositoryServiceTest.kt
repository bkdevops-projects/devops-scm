package com.tencent.devops.scm.provider.git.tgit

import TGitRepositoryService
import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.enums.Visibility
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.RepoListOptions
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.sdk.tgit.TGitProjectApi
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProject
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProjectHook
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class TGitRepositoryServiceTest : AbstractTGitServiceTest() {

    companion object {
        private const val TEST_PROJECT_HOOK_ID = 11949L
    }

    private var repositoryService: TGitRepositoryService

    init {
        `when`(tGitApi.getProjectApi()).thenReturn(mock(TGitProjectApi::class.java))

        val projectApi = tGitApi.getProjectApi()
        `when`(projectApi.getProject(TEST_PROJECT_NAME))
                .thenReturn(read("get_project.json", TGitProject::class.java))
        `when`(projectApi.getProjects("webhook_test3", null, null))
                .thenReturn(read("list_project.json", object : TypeReference<List<TGitProject>>() {}))
        `when`(projectApi.getHooks(TEST_PROJECT_NAME, null, null))
                .thenReturn(read("list_hooks.json", object : TypeReference<List<TGitProjectHook>>() {}))
        `when`(projectApi.getHook(TEST_PROJECT_NAME, TEST_PROJECT_HOOK_ID))
                .thenReturn(read("get_hook.json", TGitProjectHook::class.java))

        repositoryService = TGitRepositoryService(apiFactory)
    }

    @Test
    fun testFind() {
        val serverRepository = repositoryService.find(providerRepository)

        Assertions.assertEquals(130762L, serverRepository.id)
        Assertions.assertTrue(serverRepository.isPrivate ?: false)
        Assertions.assertFalse(serverRepository.archived ?: false)
        Assertions.assertEquals(Visibility.PRIVATE, serverRepository.visibility)
        Assertions.assertEquals("webhook_test3", serverRepository.name)
        Assertions.assertEquals("mingshewhe", serverRepository.group)
        Assertions.assertEquals("mingshewhe/webhook_test3", serverRepository.fullName)
        Assertions.assertEquals("master", serverRepository.defaultBranch)
        Assertions.assertEquals(
            "git@git.code.tencent.com:mingshewhe/webhook_test3.git",
            serverRepository.sshUrl
        )
        Assertions.assertEquals(
            "https://git.code.tencent.com/mingshewhe/webhook_test3.git",
            serverRepository.httpUrl
        )
        Assertions.assertEquals(
            "https://git.code.tencent.com/mingshewhe/webhook_test3",
            serverRepository.webUrl
        )
    }

    @Test
    fun testList() {
        val opts = RepoListOptions(repoName = "webhook_test3")
        val serverRepositories = repositoryService.list(providerRepository.auth!!, opts)
        Assertions.assertEquals(2, serverRepositories.size)
    }

    @Test
    fun testGetHook() {
        val hook = repositoryService.getHook(providerRepository, TEST_PROJECT_HOOK_ID)

        Assertions.assertEquals(11949, hook.id)
        Assertions.assertEquals(
            "https://devops.example.com/process/api/external/scm/codetgit/commit",
            hook.url
        )
        Assertions.assertTrue(hook.events?.pullRequest ?: false)
    }

    @Test
    fun listHooks() {
        val hooks = repositoryService.listHooks(providerRepository, ListOptions())
        Assertions.assertEquals(3, hooks.size)
    }
}
