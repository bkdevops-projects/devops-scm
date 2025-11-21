package com.tencent.devops.scm.provider.git.bkcode

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.enums.Visibility
import com.tencent.devops.scm.api.pojo.BranchListOptions
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.RepoListOptions
import com.tencent.devops.scm.api.pojo.TagListOptions
import com.tencent.devops.scm.sdk.bkcode.BkCodeApi
import com.tencent.devops.scm.sdk.bkcode.BkCodeBranchesApi
import com.tencent.devops.scm.sdk.bkcode.BkCodeCommitApi
import com.tencent.devops.scm.sdk.bkcode.BkCodeConstants.DEFAULT_PAGE
import com.tencent.devops.scm.sdk.bkcode.BkCodeConstants.DEFAULT_PER_PAGE
import com.tencent.devops.scm.sdk.bkcode.BkCodeProjectApi
import com.tencent.devops.scm.sdk.bkcode.BkCodeTagApi
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeDiffFileRevRange
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeBranch
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitDetail
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeDiff
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodePage
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeRepositoryDetail
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeTag
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeWebhookConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class BkCodeRefServiceTest : AbstractBkCodeServiceTest() {

    companion object {
        private const val TEST_PROJECT_BRANCH_NAME = "main"
        private const val TEST_PROJECT_TAG_NAME = "v1.0.0"
        private const val TEST_PROJECT_COMMIT_SHA = "7eaa451cdfd155338f113f1bb2d57527f34fc657"
        private const val TEST_PROJECT_BEFORE_COMMIT_SHA = "7eaa451cdfd155338f113f1bb2d57527f34fc657"
    }

    private lateinit var refService: BkCodeRefService

    init {
        apiFactory = createBkCodeApiFactory()
        refService = BkCodeRefService(apiFactory)
        mockData()
    }

    private fun mockData() {
        apiFactory = mockBkCodeApiFactory()
        refService = BkCodeRefService(apiFactory)
        `when`(apiFactory.fromAuthProvider(Mockito.any())).thenReturn(bkCodeApi)
        `when`(bkCodeApi.branchesApi).thenReturn(mock(BkCodeBranchesApi::class.java))
        val branchesApi = bkCodeApi.branchesApi
        `when`(
            branchesApi.getBranches(TEST_PROJECT_NAME)
        ).thenReturn(
            read(
                "get_branch_list.json",
                object : TypeReference<BkCodeResult<BkCodePage<BkCodeBranch>>>() {}
            ).data
        )
        `when`(
            branchesApi.getBranches(
                TEST_PROJECT_NAME,
                TEST_PROJECT_BRANCH_NAME,
                DEFAULT_PAGE,
                DEFAULT_PER_PAGE
            )
        ).thenReturn(
            read(
                "get_branch_list.json",
                object : TypeReference<BkCodeResult<BkCodePage<BkCodeBranch>>>() {}
            ).data
        )
        `when`(
            branchesApi.getBranch(
                TEST_PROJECT_NAME,
                TEST_PROJECT_BRANCH_NAME
            )
        ).thenReturn(
            read(
                "get_branch_detail.json",
                object : TypeReference<BkCodeResult<BkCodeBranch>>() {}
            ).data
        )
        `when`(bkCodeApi.tagApi).thenReturn(mock(BkCodeTagApi::class.java))
        val tagApi = bkCodeApi.tagApi
        `when`(
            tagApi.getTags(TEST_PROJECT_NAME, TEST_PROJECT_TAG_NAME, DEFAULT_PAGE, DEFAULT_PER_PAGE)
        ).thenReturn(
            read(
                "get_tag_list.json",
                object : TypeReference<BkCodeResult<BkCodePage<BkCodeTag>>>() {}
            ).data
        )
        `when`(
            tagApi.getTag(TEST_PROJECT_NAME, TEST_PROJECT_TAG_NAME)
        ).thenReturn(
            read(
                "get_tag_detail.json",
                object : TypeReference<BkCodeResult<BkCodeTag>>() {}
            ).data
        )
        `when`(bkCodeApi.commitApi).thenReturn(mock(BkCodeCommitApi::class.java))
        val commitApi = bkCodeApi.commitApi
        `when`(
            commitApi.getCommit(TEST_PROJECT_NAME, TEST_PROJECT_COMMIT_SHA)
        ).thenReturn(
            read(
                "get_commit_detail.json",
                object : TypeReference<BkCodeResult<BkCodeCommitDetail>>() {}
            ).data
        )
        `when`(
            commitApi.compare(
                TEST_PROJECT_NAME,
                TEST_PROJECT_COMMIT_SHA,
                TEST_PROJECT_BEFORE_COMMIT_SHA,
                BkCodeDiffFileRevRange.DOUBLE_DOT,
                false
            )
        ).thenReturn(
            read(
                "get_commit_diff.json",
                object : TypeReference<BkCodeResult<BkCodeDiff>>() {}
            ).data
        )
    }

    @Test
    fun testGetBranches() {
        val branch = refService.listBranches(
            repository = providerRepository,
            opts = BranchListOptions(
                page = DEFAULT_PAGE,
                pageSize = DEFAULT_PER_PAGE,
                search = TEST_PROJECT_BRANCH_NAME
            )
        )
        Assertions.assertNotNull(branch)
    }

    @Test
    fun testGetBranch() {
        val branch = refService.findBranch(
            repository = providerRepository,
            name = TEST_PROJECT_BRANCH_NAME
        )
        Assertions.assertNotNull(branch)
    }

    @Test
    fun testGetTags() {
        val tags = refService.listTags(
            repository = providerRepository,
            opts = TagListOptions(
                page = DEFAULT_PAGE,
                pageSize = DEFAULT_PER_PAGE,
                search = TEST_PROJECT_TAG_NAME
            )
        )
        Assertions.assertNotNull(tags)
    }

    @Test
    fun testGetTag() {
        val tag = refService.findTag(
            repository = providerRepository,
            name = TEST_PROJECT_TAG_NAME
        )
        Assertions.assertNotNull(tag)
    }

    @Test
    fun testGetCommit() {
        val commit = refService.findCommit(
            repository = providerRepository,
            ref = TEST_PROJECT_COMMIT_SHA
        )
        Assertions.assertNotNull(commit)
    }

    @Test
    fun compareChanges() {
        val commit = refService.compareChanges(
            repository = providerRepository,
            source = TEST_PROJECT_COMMIT_SHA,
            target = TEST_PROJECT_BEFORE_COMMIT_SHA,
            opts = ListOptions()
        )
        Assertions.assertNotNull(commit)
    }
}
