package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.tencent.devops.scm.sdk.common.ResponseResult;
import lombok.Data;

/**
 * BkCode 接口响应
 */
@Data
public class BkCodeResult<T> implements ResponseResult {
    private T data;

    private String status;
}
