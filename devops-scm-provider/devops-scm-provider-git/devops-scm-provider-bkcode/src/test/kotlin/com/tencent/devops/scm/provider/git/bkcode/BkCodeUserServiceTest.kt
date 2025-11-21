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
import com.tencent.devops.scm.sdk.bkcode.BkCodeUserApi
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeBranch
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitDetail
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodePage
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeRepositoryDetail
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeTag
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeUser
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeWebhookConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class BkCodeUserServiceTest : AbstractBkCodeServiceTest() {
    private lateinit var userService: BkCodeUserService

    init {
        apiFactory = createBkCodeApiFactory()
        userService = BkCodeUserService(apiFactory)
         mockData()
    }

    private fun mockData() {
        apiFactory = mockBkCodeApiFactory()
        userService = BkCodeUserService(apiFactory)
        val userApi: BkCodeUserApi = mock()
        `when`(apiFactory.fromAuthProvider(Mockito.any())).thenReturn(bkCodeApi)
        `when`(bkCodeApi.userApi).thenReturn(userApi)
        `when`(
            userApi.currentUser
        ).thenReturn(
            read(
                "get_current_user.json",
                object : TypeReference<BkCodeResult<BkCodeUser>>() {}
            ).data
        )
    }

    @Test
    fun testCurrentUser() {
        val user = userService.find(providerRepository.auth!!)
        Assertions.assertNotNull(user)
    }
}
