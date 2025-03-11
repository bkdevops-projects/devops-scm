package com.tencent.devops.scm.api.exception;

/**
 * 重复键异常
 */
public class DuplicateKeyScmApiException extends ScmApiException {

    public DuplicateKeyScmApiException(String message) {
        super(message);
    }
}
