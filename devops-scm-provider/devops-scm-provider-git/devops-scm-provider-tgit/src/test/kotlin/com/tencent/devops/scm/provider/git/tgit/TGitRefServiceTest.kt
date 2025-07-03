package com.tencent.devops.scm.provider.git.tgit

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.pojo.BranchListOptions
import com.tencent.devops.scm.api.pojo.TagListOptions
import com.tencent.devops.scm.sdk.tgit.TGitBranchesApi
import com.tencent.devops.scm.sdk.tgit.TGitCommitsApi
import com.tencent.devops.scm.sdk.tgit.TGitTagsApi
import com.tencent.devops.scm.sdk.tgit.pojo.TGitBranch
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommit
import com.tencent.devops.scm.sdk.tgit.pojo.TGitDiff
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTag
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class TGitRefServiceTest : AbstractTGitServiceTest() {

    companion object {
        private const val TEST_BRANCH_NAME = "master"
        private const val TEST_BRANCH_SEARCH_TERM = "mr_test"
        private const val TEST_TAG_NAME_0 = "v1.0.1"
        private const val TEST_COMMIT_SHA = "b5f141f9c1b3f87d9b070157097130be7fb7563a"
    }

    private var refService: TGitRefService

    init {
        `when`(tGitApi.branchesApi).thenReturn(mock(TGitBranchesApi::class.java))
        `when`(tGitApi.tagsApi).thenReturn(mock(TGitTagsApi::class.java))
        `when`(tGitApi.commitsApi).thenReturn(mock(TGitCommitsApi::class.java))

        val branchesApi = tGitApi.branchesApi
        `when`(branchesApi.getBranch(TEST_PROJECT_NAME, TEST_BRANCH_NAME))
                .thenReturn(read("get_branch.json", TGitBranch::class.java))
        `when`(branchesApi.getBranches(TEST_PROJECT_NAME, TEST_BRANCH_SEARCH_TERM, 1, 20))
                .thenReturn(read("search_branches.json", object : TypeReference<List<TGitBranch>>() {}))

        val tagsApi = tGitApi.tagsApi
        `when`(tagsApi.getTag(TEST_PROJECT_NAME, TEST_TAG_NAME_0))
                .thenReturn(read("get_tag.json", TGitTag::class.java))
        `when`(tagsApi.getTags(TEST_PROJECT_NAME, null, null, null))
                .thenReturn(read("list_tags.json", object : TypeReference<List<TGitTag>>() {}))

        val commitsApi = tGitApi.commitsApi
        `when`(commitsApi.getCommit(TEST_PROJECT_NAME, TEST_COMMIT_SHA))
                .thenReturn(read("get_commit.json", TGitCommit::class.java))
        `when`(commitsApi.getDiff(TEST_PROJECT_NAME, TEST_COMMIT_SHA))
                .thenReturn(read("get_commit_diff.json", object : TypeReference<List<TGitDiff>>() {}))

        refService = TGitRefService(apiFactory)
    }

    @Test
    fun testFindBranch() {
        val reference = refService.findBranch(providerRepository, TEST_BRANCH_NAME)
        Assertions.assertEquals(TEST_BRANCH_NAME, reference.name)
        Assertions.assertEquals("b5f141f9c1b3f87d9b070157097130be7fb7563a", reference.sha)
    }

    @Test
    fun testListBranches() {
        val references = refService.listBranches(
            providerRepository,
            BranchListOptions(
                search = TEST_BRANCH_SEARCH_TERM,
                pageSize = 20,
                page = 1
            )
        )

        Assertions.assertEquals(1, references.size)

        val reference = references[0]
        Assertions.assertEquals(TEST_BRANCH_SEARCH_TERM, reference.name)
        Assertions.assertEquals("9c9f8cc062060fdad67137e5e102689be765b4d4", reference.sha)
    }

    @Test
    fun testFindTag() {
        val reference = refService.findTag(providerRepository, TEST_TAG_NAME_0)
        Assertions.assertEquals(TEST_TAG_NAME_0, reference.name)
        Assertions.assertEquals("87acd380f4a91ba1eb200a082ad60f394f3062a5", reference.sha)
    }

    @Test
    fun testListTags() {
        val references = refService.listTags(providerRepository, TagListOptions())
        Assertions.assertFalse(references.isEmpty())

        val reference1 = references[0]
        Assertions.assertEquals("test", reference1.name)
        Assertions.assertEquals("b5f141f9c1b3f87d9b070157097130be7fb7563a", reference1.sha)

        val reference2 = references[1]
        Assertions.assertEquals("v1.0.1", reference2.name)
        Assertions.assertEquals("87acd380f4a91ba1eb200a082ad60f394f3062a5", reference2.sha)
    }

    @Test
    fun testFindCommit() {
        val commit = refService.findCommit(providerRepository, TEST_COMMIT_SHA)
        Assertions.assertEquals("b5f141f9c1b3f87d9b070157097130be7fb7563a", commit.sha)
        Assertions.assertEquals("webhook 10", commit.message)
        Assertions.assertEquals("mingshewhe", commit.committer?.name)
        Assertions.assertEquals("mingshewhe", commit.author?.name)
    }
}
