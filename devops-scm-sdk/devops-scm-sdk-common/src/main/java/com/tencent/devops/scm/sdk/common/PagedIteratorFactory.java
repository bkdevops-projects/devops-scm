package com.tencent.devops.scm.sdk.common;

import java.util.Iterator;

public interface PagedIteratorFactory {

    <T> Iterator<T[]> create(ScmApiClient client, ScmRequest request, Class<T[]> receiverType);
}
