package com.tencent.devops.scm.api;

import com.tencent.devops.scm.api.pojo.HookRequest;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;
import java.util.List;

public interface WebhookParser {

    Webhook parse(HookRequest request);

    /**
     * 验证webhook合法性
     *
     * @param request 请求体
     * @param secretToken 签名token
     */
    boolean verify(HookRequest request, String secretToken);
}
