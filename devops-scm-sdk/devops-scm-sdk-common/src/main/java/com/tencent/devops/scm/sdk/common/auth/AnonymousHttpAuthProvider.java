package com.tencent.devops.scm.sdk.common.auth;

import com.tencent.devops.scm.sdk.common.ScmRequest;

/**
 * 接口不需要鉴权
 */
public class AnonymousHttpAuthProvider implements HttpAuthProvider {
    @Override
    public void authorization(ScmRequest.Builder<?> builder) {
    }
}
