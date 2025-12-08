package com.tencent.devops.scm.provider.git.bkcode

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.sdk.bkcode.BkCodeApi
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiFactory
import com.tencent.devops.scm.sdk.common.connector.okhttp3.OkHttpScmConnector
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil
import okhttp3.OkHttpClient
import org.mockito.Mockito.mock
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

abstract class AbstractBkCodeServiceTest {
    companion object {
        const val TEST_PROJECT_NAME = "devops/trigger_repo"
        const val TEST_BK_CODE_API_URL = "TEST_BK_CODE_API_URL"
        const val TEST_BK_CODE_PRIVATE_TOKEN = "TEST_BK_CODE_PRIVATE_TOKEN"
    }

    var providerRepository: GitScmProviderRepository
    var apiFactory: BkCodeApiFactory
    var bkCodeApi: BkCodeApi

    init {
        providerRepository = createProviderRepository()
        apiFactory = mockBkCodeApiFactory()
        bkCodeApi = mock(BkCodeApi::class.java)
    }

    fun mockBkCodeApiFactory(): BkCodeApiFactory {
        return mock(BkCodeApiFactory::class.java)
    }

    fun createBkCodeApiFactory(): BkCodeApiFactory {
        val connector = OkHttpScmConnector(OkHttpClient.Builder().build())
        val apiUrl = getProperty(TEST_BK_CODE_API_URL)
        return BkCodeApiFactory(apiUrl, connector)
    }

    private fun createProviderRepository(): GitScmProviderRepository {
        return GitScmProviderRepository(
            auth = PersonalAccessTokenScmAuth(getProperty(TEST_BK_CODE_PRIVATE_TOKEN) ?: ""),
            projectIdOrPath = TEST_PROJECT_NAME
        )
    }

    protected fun <T> read(fileName: String, clazz: Class<T>): T {
        try {
            val filePath = this::class.java.classLoader.getResource(fileName)?.file
                ?: throw IOException("Resource not found: $fileName")
            val jsonString = File(filePath).readText(StandardCharsets.UTF_8)
            return ScmJsonUtil.fromJson(jsonString, clazz)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    protected fun <T> read(fileName: String, typeReference: TypeReference<T>): T {
        try {
            val filePath = this::class.java.classLoader.getResource(fileName)?.file
                ?: throw IOException("Resource not found: $fileName")
            val jsonString = File(filePath).readText(StandardCharsets.UTF_8)
            return ScmJsonUtil.fromJson(jsonString, typeReference)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    protected fun getProperty(key: String): String? {
        return System.getProperty(key) ?: System.getenv(key)
    }
}