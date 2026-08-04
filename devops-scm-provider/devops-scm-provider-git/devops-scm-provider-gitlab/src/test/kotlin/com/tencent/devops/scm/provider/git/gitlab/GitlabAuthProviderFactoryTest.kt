package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.pojo.auth.AccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth
import com.tencent.devops.scm.api.pojo.auth.UserPassScmAuth
import com.tencent.devops.scm.provider.git.gitlab.auth.GitlabAuthProviderFactory
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GitlabAuthProviderFactoryTest {
    @Test
    fun `supports only oauth pat and private token credentials`() {
        val supported = listOf(
            AccessTokenScmAuth("oauth"),
            PersonalAccessTokenScmAuth("pat"),
            TokenSshPrivateKeyScmAuth("private-token", "private-key", "passphrase")
        )
        supported.forEach {
            assertTrue(GitlabAuthProviderFactory.support(it))
            assertNotNull(GitlabAuthProviderFactory.create(it))
        }

        val unsupported = UserPassScmAuth("user", "password")
        assertFalse(GitlabAuthProviderFactory.support(unsupported))
        assertThrows(UnsupportedOperationException::class.java) {
            GitlabAuthProviderFactory.create(unsupported)
        }
    }
}
