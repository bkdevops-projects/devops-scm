package com.tencent.devops.scm.sdk.common.connector;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

public interface ScmConnectorRequest {

    /**
     * 请求方法
     *
     */
    String method();

    /**
     * 所有的请求头
     */
    Map<String, List<String>> allHeaders();

    /**
     * 得到请求头
     */
    String header(String name);

    /**
     * 请求content type
     */
    String contentType();

    /**
     * 获取请求体
     */
    InputStream body();

    /**
     * 请求url
     */
    URL url();

    /**
     * 是否发送请求体
     * @return true, body不为空,否则false
     */
    boolean hasBody();
}
