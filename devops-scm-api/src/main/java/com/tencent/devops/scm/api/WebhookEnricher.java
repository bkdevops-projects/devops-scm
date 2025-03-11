package com.tencent.devops.scm.api;

import com.tencent.devops.scm.api.exception.UnAuthorizedScmApiException;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;

/**
 * webhook增强器
 */
public interface WebhookEnricher {

    /**
     * 通过调用api接口补充webhook信息
     * 传入的授权信息可能已经失效或者没有权限,应该抛出UnAuthorizedScmApiException异常,
     * 后台会捕获该异常并查找有效的token进行重试
     *
     * @param webhook com.tencent.devops.scm.WebhookService#parse解析的对象
     */
    Webhook enrich(ScmProviderRepository repository, Webhook webhook) throws UnAuthorizedScmApiException;
}
