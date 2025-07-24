package com.tencent.devops.scm.provider.git.gitee

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.pojo.BranchListOptions
import com.tencent.devops.scm.sdk.gitee.GiteeApi
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory
import com.tencent.devops.scm.sdk.gitee.GiteeBranchesApi
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenMock

class GiteeRefServiceTest : AbstractGiteeServiceTest() {

    companion object {
        private lateinit var refService: GiteeRefService

        @BeforeAll
        @JvmStatic
        fun setup() {
            mockData()
        }

        fun mockData() {
            giteeApiFactory = Mockito.mock(GiteeApiFactory::class.java)
            refService = GiteeRefService(giteeApiFactory)
            providerRepository = createProviderRepository()
            val giteeApi = Mockito.mock(GiteeApi::class.java)
            whenMock(giteeApiFactory.fromAuthProvider(any()))
                .thenReturn(giteeApi)
            whenMock(giteeApi.getBranchesApi()).thenReturn(Mockito.mock(GiteeBranchesApi::class.java))
            whenMock(giteeApi.getBranchesApi().getBranches(any()))
                .thenReturn(
                    read("get_branch.json", object : TypeReference<List<GiteeBranch>>() {})
                )
        }
    }

    @Test
    fun testListBranches() {
        val references = refService.listBranches(providerRepository, BranchListOptions())
        references.forEach(::println)
    }
}