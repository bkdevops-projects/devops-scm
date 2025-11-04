package com.tencent.devops.scm.sdk.common;

/**
 * 自定义响应结果接口
 * 部分平台的接口响应结果外层会包括一层状态信息，为便于统一处理，定义该接口，后续基于此接口封装响应结果
 * 如：
 * - {"status": 200, "data": {}}
 * - {"status": 200, "param": {}}
 */
public interface ResponseResult {
    Object getData();

    Object getStatus();
}
