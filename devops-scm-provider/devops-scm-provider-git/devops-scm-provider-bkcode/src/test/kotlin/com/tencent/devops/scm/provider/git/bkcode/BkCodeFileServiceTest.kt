package com.tencent.devops.scm.provider.git.bkcode

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.sdk.bkcode.BkCodeRepositoryFileApi
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeFileContent
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class BkCodeFileServiceTest : AbstractBkCodeServiceTest() {

    companion object {
        private const val TEST_PROJECT_BRANCH_NAME = "main"
        private const val TEST_PROJECT_FILE_PATH = "a/a_1/a_1.txt"
    }

    private lateinit var fileService: BkCodeFileService

    init {
        apiFactory = createBkCodeApiFactory()
        fileService = BkCodeFileService(apiFactory)
        mockData()
    }

    private fun mockData() {
        apiFactory = mockBkCodeApiFactory()
        fileService = BkCodeFileService(apiFactory)
        `when`(apiFactory.fromAuthProvider(Mockito.any())).thenReturn(bkCodeApi)
        `when`(bkCodeApi.fileApi).thenReturn(mock(BkCodeRepositoryFileApi::class.java))
        val fileApi = bkCodeApi.fileApi
        `when`(
            fileApi.getFileContent(TEST_PROJECT_NAME, TEST_PROJECT_FILE_PATH, TEST_PROJECT_BRANCH_NAME)
        ).thenReturn(
            read(
                "get_file_content.json",
                object : TypeReference<BkCodeResult<BkCodeFileContent>>() {}
            ).data
        )
    }

    @Test
    fun testFind() {
        val fileContent = fileService.find(
            repository = providerRepository,
            path = TEST_PROJECT_FILE_PATH,
            ref = TEST_PROJECT_BRANCH_NAME
        )
        Assertions.assertEquals(
            fileContent.content,
            "123 \n123\n123\n123\n123\n123\n123\n123\n123\n123\n123\n"
        )
        Assertions.assertEquals(
            fileContent.path,
            "a/a_1/a_1.txt"
        )
    }
}
