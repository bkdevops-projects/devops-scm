package com.tencent.devops.scm.spring.manager;

import com.tencent.devops.scm.api.ScmProvider;
import com.tencent.devops.scm.api.enums.ScmProviderCodes;
import com.tencent.devops.scm.provider.svn.tsvn.TSvnScmProvider;
import com.tencent.devops.scm.spring.properties.ScmProviderProperties;

public class TSvnScmProviderFactory implements ScmProviderFactory {

    @Override
    public Boolean support(ScmProviderProperties properties) {
        return ScmProviderCodes.TSVN.name().equals(properties.getProviderCode());
    }

    @Override
    public ScmProvider build(ScmProviderProperties properties, boolean tokenApi) {
        return new TSvnScmProvider();
    }
}
