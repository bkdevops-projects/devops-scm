package com.tencent.devops.scm.spring.manager;

import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.spring.properties.HttpClientProperties;

public interface ScmConnectorFactory {

    ScmConnector create(HttpClientProperties properties);
}
