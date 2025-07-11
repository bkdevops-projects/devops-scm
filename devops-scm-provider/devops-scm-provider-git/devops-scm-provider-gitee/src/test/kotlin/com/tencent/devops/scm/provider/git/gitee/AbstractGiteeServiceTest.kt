package com.tencent.devops.scm.provider.git.gitee

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.sdk.common.connector.okhttp3.OkHttpScmConnector
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory
import okhttp3.OkHttpClient
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

open class AbstractGiteeServiceTest {

    companion object {
        protected const val TEST_PROJECT_NAME = "Tencent-BlueKing/bk-ci"
        protected const val TEST_TGIT_API_URL = "TEST_GITEE_API_URL"
        protected const val TEST_TGIT_PRIVATE_TOKEN = "TEST_GITEE_PRIVATE_TOKEN"

        var providerRepository: GitScmProviderRepository
        var giteeApiFactory: GiteeApiFactory

        init {
            providerRepository = createProviderRepository()
            giteeApiFactory = GiteeApiFactory(
                getProperty(TEST_TGIT_API_URL) ?: "",
                OkHttpScmConnector(OkHttpClient.Builder().build())
            )
        }

        fun createProviderRepository(): GitScmProviderRepository {
            return GitScmProviderRepository(
                auth = PersonalAccessTokenScmAuth(getProperty(TEST_TGIT_PRIVATE_TOKEN) ?: ""),
                projectIdOrPath = TEST_PROJECT_NAME
            )
        }

        fun <T> read(fileName: String, clazz: Class<T>): T {
            try {
                val filePath = AbstractGiteeServiceTest::class.java.classLoader.getResource(fileName)?.file
                    ?: throw IOException("Resource not found: $fileName")
                val jsonString = FileUtils.readFileToString(File(filePath), StandardCharsets.UTF_8)
                return ScmJsonUtil.fromJson(jsonString, clazz)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        fun <T> read(fileName: String, typeReference: TypeReference<T>): T {
            try {
                val filePath = AbstractGiteeServiceTest::class.java.classLoader.getResource(fileName)?.file
                    ?: throw IOException("Resource not found: $fileName")
                val jsonString = FileUtils.readFileToString(File(filePath), StandardCharsets.UTF_8)
                return ScmJsonUtil.fromJson(jsonString, typeReference)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        protected fun getProperty(key: String): String? {
            return System.getProperty(key) ?: System.getenv(key)
        }
    }
}