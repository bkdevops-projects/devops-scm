package com.tencent.devops.scm.provider.git.gitee

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.enums.CheckRunConclusion
import com.tencent.devops.scm.api.enums.CheckRunStatus
import com.tencent.devops.scm.api.pojo.BranchListOptions
import com.tencent.devops.scm.api.pojo.CheckRunInput
import com.tencent.devops.scm.api.pojo.CheckRunOutput
import com.tencent.devops.scm.sdk.gitee.GiteeApi
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory
import com.tencent.devops.scm.sdk.gitee.GiteeBranchesApi
import com.tencent.devops.scm.sdk.gitee.GiteeCheckRunApi
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCheckRun
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import java.time.LocalDateTime
import org.mockito.Mockito.`when` as whenMock

class GiteeCheckRunServiceTest : AbstractGiteeServiceTest() {

    companion object {
        private lateinit var checkRunService: GiteeCheckRunService
    }

    private val checkRunInput = CheckRunInput(
        name = "zhansgan#test#2",
        ref = "ddeaf249973c6ea9e02747f642680d05812fd80d",
        pullRequestId = 14861176,
        status = CheckRunStatus.IN_PROGRESS,
        startedAt = LocalDateTime.now(),
        output = CheckRunOutput(
            title = "devops-scm-gitee-check#output#title",
            summary = "devops-scm-gitee-check#output#summary",
            text = "devops-scm-gitee-check#output#text"
        )
    )

    init {
        // checkRunService = GiteeCheckRunService(giteeApiFactory)
        mockData()
    }

    private fun mockData() {
        giteeApiFactory = Mockito.mock(GiteeApiFactory::class.java)
        checkRunService = GiteeCheckRunService(giteeApiFactory)
        providerRepository = createProviderRepository()
        val giteeApi = Mockito.mock(GiteeApi::class.java)
        whenMock(giteeApiFactory.fromAuthProvider(any()))
                .thenReturn(giteeApi)
        whenMock(giteeApi.checkRunApi).thenReturn(Mockito.mock(GiteeCheckRunApi::class.java))
        whenMock(giteeApi.checkRunApi.create(anyString(), any(GiteeCheckRun::class.java)))
                .thenReturn(
                    read("create_check_run.json", object : TypeReference<GiteeCheckRun>() {})
                )
        whenMock(giteeApi.checkRunApi.update(anyString(), anyLong(), any(GiteeCheckRun::class.java)))
                .thenReturn(
                    read("create_check_run.json", object : TypeReference<GiteeCheckRun>() {})
                )
    }

    @Test
    fun create() {
        val create = checkRunService.create(providerRepository, checkRunInput)
        println("create = $create")
    }



    @Test
    fun update() {
        val updateCheckRun = checkRunInput.copy(
            status = CheckRunStatus.COMPLETED,
            completedAt = LocalDateTime.now(),
            conclusion = CheckRunConclusion.SUCCESS,
            id = 23059678
        )
        checkRunService.update(providerRepository, updateCheckRun)
    }
}