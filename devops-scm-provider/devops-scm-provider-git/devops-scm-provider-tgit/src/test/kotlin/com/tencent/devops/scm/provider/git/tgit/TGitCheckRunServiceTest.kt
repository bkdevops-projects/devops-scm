package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.enums.CheckRunConclusion
import com.tencent.devops.scm.api.enums.CheckRunStatus
import com.tencent.devops.scm.api.pojo.CheckRunInput
import com.tencent.devops.scm.api.pojo.CheckRunOutput
import com.tencent.devops.scm.sdk.tgit.TGitCheckRunApi
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCheckRun
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`

class TGitCheckRunServiceTest : AbstractTGitServiceTest() {

    private var checkRunService: TGitCheckRunService

    private val checkRunInput = CheckRunInput(
        name = "devops-scm-sdk#TGitCheckRunServiceTest#create",
        ref = "534132744e6c9b2a0e7fe32814a7e05f3f827a9e",
        status = CheckRunStatus.IN_PROGRESS,
        detailsUrl = "https://github.com/bkdevops-projects/devops-scm",
        output = CheckRunOutput(
            title = "devops-scm-sdk#TGitCheckRunServiceTest#create#title",
            summary = "devops-scm-sdk#TGitCheckRunServiceTest#create#summary",
            text = "devops-scm-sdk#TGitCheckRunServiceTest#create#text"
        ),
        block = false,
        targetBranches = listOf("master")
    )

    init {
        // apiFactory = createTGitApiFactory()
        checkRunService = TGitCheckRunService(apiFactory)
        mockData()
    }

    private fun mockData() {
        `when`(tGitApi.checkRunApi).thenReturn(Mockito.mock(TGitCheckRunApi::class.java))
        val checkRunApi = tGitApi.checkRunApi
        `when`(
            checkRunApi.create(
                anyString(),
                anyString(),
                any(TGitCheckRun::class.java)
            )
        ).thenReturn(read("create_check_run.json", TGitCheckRun::class.java))
    }

    @Test
    fun create() {
        val checkRun = checkRunService.create(
            providerRepository,
            checkRunInput
        )
        Assertions.assertEquals(checkRunInput.name, checkRun.name)
        Assertions.assertEquals(checkRunInput.detailsUrl, checkRun.detailsUrl)
        Assertions.assertEquals(checkRunInput.output?.text, checkRun.detail)
        Assertions.assertEquals(checkRunInput.output?.summary, checkRun.summary)
    }

    @Test
    fun update() {
        checkRunService.update(
            providerRepository,
            checkRunInput.copy(
                status = CheckRunStatus.COMPLETED,
                conclusion = CheckRunConclusion.FAILURE,
                block = true
            )
        )
    }
}
