package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.PagedIteratorFactory;
import com.tencent.devops.scm.sdk.common.ScmApiClient;
import com.tencent.devops.scm.sdk.common.ScmRequest;

import java.util.Iterator;

public class TGitPagedIteratorFactory implements PagedIteratorFactory {

    public static TGitPagedIteratorFactory iteratorFactory = new TGitPagedIteratorFactory();

    @Override
    public <T> Iterator<T[]> create(ScmApiClient client,
                                    ScmRequest request, Class<T[]> receiverType) {
        return TGitPagedIterator.create(client, request, receiverType);
    }
}
