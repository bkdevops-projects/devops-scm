package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.exception.NotFoundScmApiException
import com.tencent.devops.scm.api.exception.ScmApiException
import com.tencent.devops.scm.api.exception.UnAuthorizedScmApiException
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.provider.git.tgit.auth.TGitAuthProviderFactory
import com.tencent.devops.scm.sdk.tgit.TGitApi
import com.tencent.devops.scm.sdk.tgit.TGitApiException
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory

/**
 * TGit API请求模板工具类
 */
object TGitApiTemplate {

    /**
     * 执行带仓库信息的API请求
     * @param repository 代码仓库信息
     * @param apiFactory TGit API工厂
     * @param apiFunction 要执行的API函数
     * @return API调用结果
     * @throws ScmApiException 当API调用失败时抛出
     */
    fun <R> execute(
        repository: ScmProviderRepository,
        apiFactory: TGitApiFactory,
        apiFunction: (GitScmProviderRepository, TGitApi) -> R
    ): R {
        try {
            val repo = repository as GitScmProviderRepository
            val tGitApi = apiFactory.fromAuthProvider(TGitAuthProviderFactory.create(repo.auth))
            return apiFunction(repo, tGitApi)
        } catch (t: Throwable) {
            throw handleThrowable(t)
        }
    }

    /**
     * 执行带认证信息的API请求
     * @param auth 认证信息
     * @param apiFactory TGit API工厂
     * @param apiFunction 要执行的API函数
     * @return API调用结果
     * @throws ScmApiException 当API调用失败时抛出
     */
    fun <R> execute(
        auth: IScmAuth,
        apiFactory: TGitApiFactory,
        apiFunction: (TGitApi) -> R
    ): R {
        try {
            val tGitApi = apiFactory.fromAuthProvider(TGitAuthProviderFactory.create(auth))
            return apiFunction(tGitApi)
        } catch (t: Throwable) {
            throw handleThrowable(t)
        }
    }

    /**
     * 转换TGit API异常为SCM异常
     * @param e TGit API异常
     * @return 转换后的SCM异常
     */
    private fun translateException(e: TGitApiException): ScmApiException {
        return when (e.statusCode) {
            404 -> NotFoundScmApiException(e.message)
            401, 403 -> UnAuthorizedScmApiException(e.message)
            else -> ScmApiException(e.message, e.statusCode)
        }
    }

    /**
     * 处理Throwable异常
     * @param t 捕获的异常
     * @return 转换后的SCM异常
     */
    private fun handleThrowable(t: Throwable): ScmApiException {
        return if (t is TGitApiException) {
            translateException(t)
        } else {
            ScmApiException(t)
        }
    }
}
