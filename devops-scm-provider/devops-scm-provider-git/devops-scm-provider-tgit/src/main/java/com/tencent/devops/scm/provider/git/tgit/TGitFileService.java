package com.tencent.devops.scm.provider.git.tgit;

import com.tencent.devops.scm.api.FileService;
import com.tencent.devops.scm.api.pojo.Content;
import com.tencent.devops.scm.api.pojo.ContentInput;
import com.tencent.devops.scm.api.pojo.Tree;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitRepositoryFile;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTreeItem;
import java.util.List;
import java.util.stream.Collectors;

public class TGitFileService implements FileService {

    private final TGitApiFactory apiFactory;

    public TGitFileService(TGitApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public Content find(ScmProviderRepository repository, String path, String ref) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            TGitRepositoryFile file = tGitApi.getRepositoryFileApi()
                    .getFile(repo.getProjectIdOrPath(), path, ref);
            return TGitObjectConverter.convertContent(file);
        });
    }

    @Override
    public void create(ScmProviderRepository repository, String path, ContentInput input) {
        TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            tGitApi.getRepositoryFileApi()
                    .createFile(repo.getProjectIdOrPath(), path,
                            input.getRef(), input.getContent(), input.getMessage());
        });
    }

    @Override
    public void update(ScmProviderRepository repository, String path, ContentInput input) {
        TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            tGitApi.getRepositoryFileApi()
                    .updateFile(repo.getProjectIdOrPath(), path,
                            input.getRef(), input.getContent(), input.getMessage());
        });
    }

    @Override
    public List<Tree> listTree(ScmProviderRepository repository, String path, String ref, boolean recursive) {
        return TGitApiTemplate.execute(repository, apiFactory, (repo, tGitApi) -> {
            List<TGitTreeItem> treeItems = tGitApi.getRepositoryFileApi()
                    .getTree(repo.getProjectIdOrPath(), path, ref, recursive);
            return treeItems.stream()
                    .map(TGitObjectConverter::convertTree)
                    .collect(Collectors.toList());
        });
    }
}
