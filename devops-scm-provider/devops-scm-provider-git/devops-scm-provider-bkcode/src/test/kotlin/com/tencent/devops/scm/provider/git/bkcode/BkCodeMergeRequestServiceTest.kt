package com.tencent.devops.scm.provider.git.bkcode

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.sdk.bkcode.BkCodeCommitApi
import com.tencent.devops.scm.sdk.bkcode.BkCodeMergeRequestApi
import com.tencent.devops.scm.sdk.bkcode.BkCodeProjectApi
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeDiffFileRevRange
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeDiff
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeMergeRequest
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeRepositoryDetail
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class BkCodeMergeRequestServiceTest : AbstractBkCodeServiceTest() {

    companion object {
        private const val TEST_MR_ID = 5L
        private const val TEST_MR_NUMBER = 2
    }

    private lateinit var mergeRequestService: BkCodeMergeRequestService

    init {
        apiFactory = createBkCodeApiFactory()
        mergeRequestService = BkCodeMergeRequestService(apiFactory)
        mockData()
    }

    private fun mockData() {
        apiFactory = mockBkCodeApiFactory()
        mergeRequestService = BkCodeMergeRequestService(apiFactory)
        `when`(apiFactory.fromAuthProvider(Mockito.any())).thenReturn(bkCodeApi)
        `when`(bkCodeApi.mergeRequestApi).thenReturn(mock(BkCodeMergeRequestApi::class.java))
        val mergeRequestApi = bkCodeApi.mergeRequestApi
        `when`(
            mergeRequestApi.getMergeRequestById(TEST_PROJECT_NAME, TEST_MR_ID)
        ).thenReturn(
            read(
                "get_mr_detail.json",
                object : TypeReference<BkCodeResult<BkCodeMergeRequest>>() {}
            ).data
        )
        `when`(
            mergeRequestApi.getMergeRequestByNumber(TEST_PROJECT_NAME, TEST_MR_NUMBER)
        ).thenReturn(
            read(
                "get_mr_detail.json",
                object : TypeReference<BkCodeResult<BkCodeMergeRequest>>() {}
            ).data
        )
        `when`(bkCodeApi.commitApi).thenReturn(mock(BkCodeCommitApi::class.java))
        val commitApi = bkCodeApi.commitApi
        `when`(
            commitApi.compare(
                TEST_PROJECT_NAME,
                "7eaa451cdfd155338f113f1bb2d57527f34fc657",
                "e98fb11ac3d6215c6d7385327a5b4763f7a89ea6",
                BkCodeDiffFileRevRange.DOUBLE_DOT,
                false
            )
        ).thenReturn(
            read(
                "get_commit_diff.json",
                object : TypeReference<BkCodeResult<BkCodeDiff>>() {}
            ).data
        )
        `when`(bkCodeApi.projectApi).thenReturn(mock(BkCodeProjectApi::class.java))
        val projectApi = bkCodeApi.projectApi
        `when`(
            projectApi.getProject(TEST_PROJECT_NAME)
        ).thenReturn(
            read(
                "get_repo_detail.json",
                object : TypeReference<BkCodeResult<BkCodeRepositoryDetail>>() {}
            ).data
        )
    }

    @Test
    fun findByNum() {
        val branch = mergeRequestService.find(
            repository = providerRepository,
            number = TEST_MR_NUMBER
        )
        Assertions.assertNotNull(branch)
    }

    @Test
    fun listChanges() {
        val changes = mergeRequestService.listChanges(
            repository = providerRepository,
            number = TEST_MR_NUMBER,
            opts = ListOptions()
        )
        Assertions.assertNotNull(changes)
    }
}
