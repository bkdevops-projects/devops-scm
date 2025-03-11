package com.tencent.devops.scm.sdk.common.connector;

/**
 * 检测和执行在 http 请求期间发生错误时要执行的操作。
 */
public interface ScmConnectorResponseErrorHandler {

    Boolean isError(ScmConnectorResponse connectorResponse);

    void onError(ScmConnectorResponse connectorResponse);
}
