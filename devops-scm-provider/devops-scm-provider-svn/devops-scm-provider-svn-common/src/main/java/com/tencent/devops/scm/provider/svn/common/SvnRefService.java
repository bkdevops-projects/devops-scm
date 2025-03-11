package com.tencent.devops.scm.provider.svn.common;

import com.tencent.devops.scm.api.RefService;
import com.tencent.devops.scm.api.exception.ScmApiException;
import com.tencent.devops.scm.api.pojo.BranchListOptions;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.CommitListOptions;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.api.pojo.ReferenceInput;
import com.tencent.devops.scm.api.pojo.TagListOptions;
import com.tencent.devops.scm.api.pojo.Tree;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

public class SvnRefService implements RefService {

    @Override
    public void createBranch(ScmProviderRepository repository, ReferenceInput input) {

    }

    @Override
    public Reference findBranch(ScmProviderRepository repository, String name) {
        throw new UnsupportedOperationException("svn not support find branch");
    }

    @Override
    public List<Reference> listBranches(ScmProviderRepository repository, BranchListOptions opts) {
        SvnScmProviderRepository providerRepository = (SvnScmProviderRepository) repository;
        try {
            SVNRepository svnRepository = SvnkitUtils.openRepo(providerRepository);
            List<Tree> trees = SvnkitUtils.listFiles(svnRepository, "", -1, false);
            return trees.stream().map(tree ->
                    Reference.builder()
                            .name(tree.getPath())
                            .sha(tree.getSha())
                            .build()
            ).collect(Collectors.toList());
        } catch (SVNException e) {
            throw new ScmApiException(e);
        }
    }

    @Override
    public void createTag(ScmProviderRepository repository, ReferenceInput input) {

    }

    @Override
    public Reference findTag(ScmProviderRepository repository, String name) {
        throw new UnsupportedOperationException("svn not support find branch");
    }

    @Override
    public List<Reference> listTags(ScmProviderRepository repository, TagListOptions opts) {
        return Collections.emptyList();
    }

    @Override
    public Commit findCommit(ScmProviderRepository repository, String ref) {
        return null;
    }

    @Override
    public List<Commit> listCommits(ScmProviderRepository repository, CommitListOptions opts) {
        return Collections.emptyList();
    }

    @Override
    public List<Change> listChanges(ScmProviderRepository repository, String ref, ListOptions opts) {

        return Collections.emptyList();
    }

    @Override
    public List<Change> compareChanges(ScmProviderRepository repository, String source, String target,
            ListOptions opts) {
        return Collections.emptyList();
    }
}
