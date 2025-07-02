package com.tencent.devops.scm.provider.git.tgit

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.pojo.auth.PrivateTokenScmAuth
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.provider.git.tgit.auth.TGitAuthProviderFactory
import com.tencent.devops.scm.sdk.common.connector.okhttp3.OkHttpScmConnector
import com.tencent.devops.scm.sdk.tgit.TGitApi
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory
import com.tencent.devops.scm.sdk.tgit.util.TGitJsonUtil
import okhttp3.OkHttpClient
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

abstract class AbstractTGitServiceTest {
    companion object {
        const val TEST_PROJECT_NAME = "mingshewhe/webhook_test3"
        const val TEST_TGIT_API_URL = "TEST_TGIT_API_URL"
        const val TEST_TGIT_PRIVATE_TOKEN = "TEST_TGIT_PRIVATE_TOKEN"
    }

    var providerRepository: GitScmProviderRepository
    var apiFactory: TGitApiFactory
    var tGitApi: TGitApi

    init {
        providerRepository = createProviderRepository()
        apiFactory = mockTGitApiFactory()
        `when`(apiFactory.fromAuthProvider(any())).thenReturn(mock())
        tGitApi = apiFactory.fromAuthProvider(TGitAuthProviderFactory.create(providerRepository.auth!!))
    }

    private fun mockTGitApiFactory(): TGitApiFactory {
        return mock(TGitApiFactory::class.java)
    }

    fun createTGitApiFactory(): TGitApiFactory {
        val connector = OkHttpScmConnector(OkHttpClient.Builder().build())
        val apiUrl = getProperty(TEST_TGIT_API_URL)
        return TGitApiFactory(apiUrl, connector)
    }

    private fun createProviderRepository(): GitScmProviderRepository {
        return GitScmProviderRepository(
            auth = PrivateTokenScmAuth(getProperty(TEST_TGIT_PRIVATE_TOKEN) ?: ""),
            projectIdOrPath = TEST_PROJECT_NAME
        )
    }

    protected fun <T> read(fileName: String, clazz: Class<T>): T {
        try {
            val filePath = this::class.java.classLoader.getResource(fileName)?.file
                ?: throw IOException("Resource not found: $fileName")
            val jsonString = File(filePath).readText(StandardCharsets.UTF_8)
            return TGitJsonUtil.fromJson(jsonString, clazz)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    protected fun <T> read(fileName: String, typeReference: TypeReference<T>): T {
        try {
            val filePath = this::class.java.classLoader.getResource(fileName)?.file
                ?: throw IOException("Resource not found: $fileName")
            val jsonString = File(filePath).readText(StandardCharsets.UTF_8)
            return TGitJsonUtil.fromJson(jsonString, typeReference)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    protected fun getProperty(key: String): String? {
        return System.getProperty(key) ?: System.getenv(key)
    }
}