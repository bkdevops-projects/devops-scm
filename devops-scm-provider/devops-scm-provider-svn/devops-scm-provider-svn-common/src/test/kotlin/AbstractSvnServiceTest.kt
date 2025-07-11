package com.tencent.devops.scm.provider.svn.common

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.auth.UserPassScmAuth
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository

open class AbstractSvnServiceTest {
    companion object {
        protected const val TEST_SVN_URL = "TEST_SVN_URL"
        protected const val TEST_SVN_USER_NAME = "TEST_SVN_USERNAME"
        protected const val TEST_SVN_PASSWORD = "TEST_SVN_PASSWORD"

        protected fun getProperty(key: String): String? {
            return System.getProperty(key) ?: System.getenv(key)
        }
    }


    private var providerRepository: SvnScmProviderRepository? = null

    init {
        providerRepository = createProviderRepository()
    }

    protected open fun createProviderRepository(): SvnScmProviderRepository {
        val url = getProperty(TEST_SVN_URL) ?: ""
        val userName = getProperty(TEST_SVN_USER_NAME) ?: ""
        val password = getProperty(TEST_SVN_PASSWORD) ?: ""
        val auth: IScmAuth = UserPassScmAuth(userName, password)
        return SvnScmProviderRepository(
            url = url,
            auth = auth
        )
    }
}