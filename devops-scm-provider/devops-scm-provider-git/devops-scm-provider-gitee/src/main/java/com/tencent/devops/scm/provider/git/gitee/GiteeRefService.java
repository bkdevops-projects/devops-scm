package com.tencent.devops.scm.provider.git.gitee;

import com.tencent.devops.scm.api.RefService;
import com.tencent.devops.scm.api.pojo.BranchListOptions;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.CommitListOptions;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.api.pojo.ReferenceInput;
import com.tencent.devops.scm.api.pojo.TagListOptions;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import com.tencent.devops.scm.provider.git.gitee.auth.GiteeTokenAuthProviderAdapter;
import com.tencent.devops.scm.sdk.common.connector.okhttp3.OkHttpScmConnector;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeBranchesApi;
import com.tencent.devops.scm.sdk.gitee.auth.GiteeTokenAuthProvider;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

public class GiteeRefService implements RefService {

    @Override
    public void createBranch(ScmProviderRepository repository, ReferenceInput input) {

    }

    @Override
    public Reference findBranch(ScmProviderRepository repository, String name) {
        return null;
    }

    @Override
    public List<Reference> listBranches(ScmProviderRepository repository, BranchListOptions opts) {
        GitScmProviderRepository gitScmProviderRepository = (GitScmProviderRepository) repository;
        // 校验授权类型
        if (!GiteeTokenAuthProviderAdapter.support(gitScmProviderRepository.getAuth())) {
            return new ArrayList<>();
        }
        // 获取授权提供者
        GiteeTokenAuthProvider giteeTokenAuthProvider = GiteeTokenAuthProviderAdapter.get(
                gitScmProviderRepository.getAuth()
        );
        // 组装请求连接器, 可根据情况自行调整连接器相关配置
        OkHttpClient client = new Builder().build();
        OkHttpScmConnector okHttpScmConnector = new OkHttpScmConnector(client);
        // 构建请求接口类
        GiteeBranchesApi branchesApi = new GiteeApi(
                "https://gitee.com/api/v5",
                okHttpScmConnector,
                giteeTokenAuthProvider
        ).getBranchesApi();
        List<GiteeBranch> branches = branchesApi.getBranches(gitScmProviderRepository.getProjectIdOrPath());
        // 结果转化
        return branches.stream().map(GiteeObjectConverter::convertBranches).collect(Collectors.toList());
    }

    @Override
    public void createTag(ScmProviderRepository repository, ReferenceInput input) {

    }

    @Override
    public Reference findTag(ScmProviderRepository repository, String name) {
        return null;
    }

    @Override
    public List<Reference> listTags(ScmProviderRepository repository, TagListOptions opts) {
        return null;
    }

    @Override
    public Commit findCommit(ScmProviderRepository repository, String ref) {
        return null;
    }

    @Override
    public List<Commit> listCommits(ScmProviderRepository repository, CommitListOptions opts) {
        return null;
    }

    @Override
    public List<Change> listChanges(ScmProviderRepository repository, String ref, ListOptions opts) {
        return null;
    }

    @Override
    public List<Change> compareChanges(
            ScmProviderRepository repository,
            String source,
            String target,
            ListOptions opts
    ) {
        return null;
    }
}
