package com.tencent.devops.scm.provider.common

import com.tencent.devops.scm.api.exception.NotFoundScmApiException
import com.tencent.devops.scm.api.exception.ScmApiException
import com.tencent.devops.scm.api.exception.UnAuthorizedScmApiException
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.common.exception.BaseScmApiException

/**
 * SCM API请求模板接口
 * 统一创建API对象并捕获异常
 */
interface ScmApiTemplate<T, F, V : ScmProviderRepository, E : BaseScmApiException> {

    /**
     * 获取API对象
     */
    fun getApi(repository: V, apiFactory: F): T

    fun getApi(auth: IScmAuth, apiFactory: F): T

    /**
     * 执行带仓库信息的API请求
     * @param repository 代码仓库信息
     * @param apiFunction 要执行的API函数
     * @return API调用结果
     * @throws ScmApiException 当API调用失败时抛出
     */
    fun <R> execute(
        repository: V,
        apiFactory: F,
        apiFunction: (V, T) -> R
    ): R {
        try {
            val tGitApi = getApi(repository, apiFactory)
            return apiFunction(repository, tGitApi)
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
        apiFactory: F,
        apiFunction: (T) -> R
    ): R {
        try {
            return apiFunction(getApi(auth, apiFactory))
        } catch (t: Throwable) {
            throw handleThrowable(t)
        }
    }

    /**
     * 转换API异常为SCM异常
     * @param e API异常
     * @return 转换后的SCM异常
     */
    private fun translateException(e: E): ScmApiException {
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
    @Suppress("UNCHECKED_CAST")
    private fun handleThrowable(t: Throwable): ScmApiException {
        val err = t as? E
        return if (err != null) {
            translateException(t)
        } else {
            ScmApiException(t)
        }
    }
}
