package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.pojo.PullRequestInput
import com.tencent.devops.scm.sdk.tgit.TGitApi
import com.tencent.devops.scm.sdk.tgit.TGitMergeRequestApi
import com.tencent.devops.scm.sdk.tgit.TGitProjectApi
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequest
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequestParams
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class TGitPullRequestServiceTest : AbstractTGitServiceTest() {

    private lateinit var pullRequestService: TGitPullRequestService

    init {
        `when`(tGitApi.mergeRequestApi).thenReturn(mock(TGitMergeRequestApi::class.java))
        `when`(tGitApi.projectApi).thenReturn(mock(TGitProjectApi::class.java))
        val projectApi = tGitApi.projectApi
        val mergeRequestApi = tGitApi.mergeRequestApi
        `when`(mergeRequestApi.createMergeRequest(anyString(), any(TGitMergeRequestParams::class.java)))
                .thenReturn(read("create_merge_request.json", TGitMergeRequest::class.java))
        `when`(projectApi.getProject(any()))
                .thenReturn(read("get_project.json", TGitProject::class.java))
        pullRequestService = TGitPullRequestService(apiFactory)
    }

    @Test
    fun createMergeRequest() {
        val requestInput = PullRequestInput(
            title = "devops触发调试",
            body = null,
            sourceBranch = "feat_101",
            targetBranch = "master"
        )
        val create = pullRequestService.create(
            providerRepository,
            requestInput
        )
        Assertions.assertEquals(requestInput.title, create.title)
        Assertions.assertEquals(requestInput.sourceBranch, create.sourceRef.name)
        Assertions.assertEquals(requestInput.targetBranch, create.targetRef.name)
    }
}
