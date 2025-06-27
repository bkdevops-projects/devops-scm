package com.tencent.devops.scm.api

import com.tencent.devops.scm.api.pojo.HookRequest
import com.tencent.devops.scm.api.pojo.webhook.Webhook

/**
 * Webhook解析器接口
 */
interface WebhookParser {
    /**
     * 解析webhook请求
     * @param request 请求对象
     * @return 解析后的webhook信息
     */
    fun parse(request: HookRequest): Webhook

    /**
     * 验证webhook合法性
     * @param request 请求体
     * @param secretToken 签名token
     * @return 验证结果
     */
    fun verify(request: HookRequest, secretToken: String): Boolean
}
