package com.tencent.devops.scm.provider.git.tgit

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.enums.ContentKind
import com.tencent.devops.scm.sdk.tgit.TGitRepositoryFileApi
import com.tencent.devops.scm.sdk.tgit.pojo.TGitRepositoryFile
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTreeItem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class TGitFileServiceTest : AbstractTGitServiceTest() {

    companion object {
        private const val TEST_BRANCH_NAME = "master"
        private const val TEST_FILE_PATH = "mr.txt"
    }

    private lateinit var fileService: TGitFileService

    init {
        `when`(tGitApi.getRepositoryFileApi()).thenReturn(mock(TGitRepositoryFileApi::class.java))
        val repositoryFileApi = tGitApi.getRepositoryFileApi()

        `when`(repositoryFileApi.getFile(TEST_PROJECT_NAME, TEST_FILE_PATH, TEST_BRANCH_NAME))
            .thenReturn(read("get_file.json", TGitRepositoryFile::class.java))
        `when`(tGitApi.getRepositoryFileApi().getTree(TEST_PROJECT_NAME, "", TEST_BRANCH_NAME, true))
            .thenReturn(read("get_file_tree.json", object : TypeReference<List<TGitTreeItem>>() {}))

        fileService = TGitFileService(apiFactory)
    }

    @Test
    fun testFind() {
        val content = fileService.find(providerRepository, TEST_FILE_PATH, TEST_BRANCH_NAME)
        Assertions.assertEquals("mr.txt", content.path)
        Assertions.assertEquals(
            "mr 1\n" +
                "mr 2\n" +
                "mr 3\n" +
                "mr 4\n" +
                "mr 5\n" +
                "mr 6", content.content
        )
        Assertions.assertEquals("b5f141f9c1b3f87d9b070157097130be7fb7563a", content.sha)
        Assertions.assertEquals("f2395fe57c34bb308725a686a623e369d2fc36d2", content.blobId)
    }

    @Test
    fun testListTree() {
        val trees = fileService.listTree(providerRepository, "", TEST_BRANCH_NAME, true)
        Assertions.assertEquals(3, trees.size)

        val tree = trees[0]
        Assertions.assertEquals("a11994a2ba987a3da6d71c56e6edc3e7c182d50f", tree.blobId)
        Assertions.assertEquals("README.md", tree.path)
        Assertions.assertEquals(ContentKind.FILE, tree.kind)
    }
}
