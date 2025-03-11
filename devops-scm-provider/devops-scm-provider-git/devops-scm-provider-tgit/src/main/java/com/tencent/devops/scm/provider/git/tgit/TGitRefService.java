package com.tencent.devops.scm.provider.git.tgit;

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
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitBranch;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommit;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCompareResults;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitDiff;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTag;
import java.util.List;
import java.util.stream.Collectors;

public class TGitRefService implements RefService {

    private final TGitApiFactory apiFactory;

    public TGitRefService(TGitApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public void createBranch(ScmProviderRepository repository, ReferenceInput input) {
        TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            tGitApi.getBranchesApi().createBranch(repo.getProjectIdOrPath(), input.getName(), input.getSha());
        });
    }

    @Override
    public Reference findBranch(ScmProviderRepository repository, String name) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitBranch branch = tGitApi.getBranchesApi().getBranch(repo.getProjectIdOrPath(), name);
            return TGitObjectConverter.convertBranch(branch);
        });
    }

    @Override
    public List<Reference> listBranches(ScmProviderRepository repository, BranchListOptions opts) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            List<TGitBranch> branches = tGitApi.getBranchesApi()
                    .getBranches(repo.getProjectIdOrPath(), opts.getSearch(), opts.getPage(), opts.getPageSize());
            return branches.stream()
                    .map(TGitObjectConverter::convertBranch)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public void createTag(ScmProviderRepository repository, ReferenceInput input) {
        TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            tGitApi.getTagsApi().createTag(repo.getProjectIdOrPath(), input.getName(), input.getSha());
        });
    }

    @Override
    public Reference findTag(ScmProviderRepository repository, String name) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitTag tag = tGitApi.getTagsApi().getTag(repo.getProjectIdOrPath(), name);
            return TGitObjectConverter.convertTag(tag);
        });
    }

    /**
     * 获取tag列表
     * <pre>注意: 工蜂社区版不支持tag搜索</pre>
     */
    @Override
    public List<Reference> listTags(ScmProviderRepository repository, TagListOptions opts) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            List<TGitTag> tags = tGitApi.getTagsApi()
                    .getTags(repo.getProjectIdOrPath(), opts.getSearch(), opts.getPage(), opts.getPageSize());
            return tags.stream()
                    .map(TGitObjectConverter::convertTag)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public Commit findCommit(ScmProviderRepository repository, String ref) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitCommit commit = tGitApi.getCommitsApi().getCommit(repo.getProjectIdOrPath(), ref);
            return TGitObjectConverter.convertCommit(commit);
        });
    }

    @Override
    public List<Commit> listCommits(ScmProviderRepository repository, CommitListOptions opts) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            List<TGitCommit> commits = tGitApi.getCommitsApi()
                    .getCommits(repo.getProjectIdOrPath(), opts.getRef(), opts.getPath(), opts.getPage(),
                            opts.getPageSize());
            return commits.stream()
                    .map(TGitObjectConverter::convertCommit)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public List<Change> listChanges(ScmProviderRepository repository, String ref, ListOptions opts) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            List<TGitDiff> diffs = tGitApi.getCommitsApi().getDiff(repo.getProjectIdOrPath(), ref);
            return diffs.stream()
                    .map(TGitObjectConverter::convertChange)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public List<Change> compareChanges(ScmProviderRepository repository, String source, String target,
            ListOptions opts) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitCompareResults compareResults = tGitApi.getRepositoryApi()
                    .compare(repo.getProjectIdOrPath(), source, target, false, null);
            return compareResults.getDiffs().stream()
                    .map(TGitObjectConverter::convertChange)
                    .collect(Collectors.toList());
        });
    }
}
