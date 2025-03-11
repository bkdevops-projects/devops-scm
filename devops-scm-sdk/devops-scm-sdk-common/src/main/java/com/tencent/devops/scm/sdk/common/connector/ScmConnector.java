package com.tencent.devops.scm.sdk.common.connector;

import java.io.IOException;

/**
 * scm http连接器
 */
public interface ScmConnector {

    ScmConnectorResponse send(ScmConnectorRequest connectorRequest) throws IOException;
}
