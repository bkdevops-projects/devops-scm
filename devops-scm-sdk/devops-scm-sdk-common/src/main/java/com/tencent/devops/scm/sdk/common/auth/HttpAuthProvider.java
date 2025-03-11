package com.tencent.devops.scm.sdk.common.auth;

import com.tencent.devops.scm.sdk.common.ScmRequest;

/**
 * 接口鉴权提供者
 */
public interface HttpAuthProvider {

    HttpAuthProvider ANONYMOUS = new AnonymousHttpAuthProvider();

    void authorization(ScmRequest.Builder<?> builder);
}
