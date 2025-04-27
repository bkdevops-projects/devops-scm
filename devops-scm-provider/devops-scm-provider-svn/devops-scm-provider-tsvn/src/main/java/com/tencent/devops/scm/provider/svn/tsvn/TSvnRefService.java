package com.tencent.devops.scm.provider.svn.tsvn;


import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository;
import com.tencent.devops.scm.provider.svn.common.SvnRefService;
import com.tencent.devops.scm.provider.svn.tsvn.auth.TSvnAuthProviderAdapter;
import com.tencent.devops.scm.sdk.tsvn.TSvnApi;
import com.tencent.devops.scm.sdk.tsvn.TSvnApiFactory;
import com.tencent.devops.scm.sdk.tsvn.TSvnCommitApi;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnCommit;
import java.util.List;

public class TSvnRefService extends SvnRefService {

    private final TSvnApiFactory apiFactory;

    public TSvnRefService(TSvnApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public Reference findBranch(ScmProviderRepository repository, String name) {
        SvnScmProviderRepository repo = (SvnScmProviderRepository) repository;
        TSvnApi tSvnApi = apiFactory.fromAuthProvider(TSvnAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        TSvnCommitApi commitApi = tSvnApi.getCommitApi();
        List<TSvnCommit> projectApiHooks = commitApi.getCommit(
                repo.getProjectIdOrPath(),
                name,
                1
        );
        return projectApiHooks.stream()
                .findFirst()
                .map(TSvnObjectConverter::convertReference)
                .orElse(null);
    }
}
