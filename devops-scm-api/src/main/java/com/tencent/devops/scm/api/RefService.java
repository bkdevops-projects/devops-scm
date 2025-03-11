package com.tencent.devops.scm.api;

import com.tencent.devops.scm.api.pojo.BranchListOptions;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.CommitListOptions;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.api.pojo.ReferenceInput;
import com.tencent.devops.scm.api.pojo.TagListOptions;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import java.util.List;

public interface RefService {

    void createBranch(ScmProviderRepository repository, ReferenceInput input);

    Reference findBranch(ScmProviderRepository repository, String name);

    List<Reference> listBranches(ScmProviderRepository repository, BranchListOptions opts);

    void createTag(ScmProviderRepository repository, ReferenceInput input);

    Reference findTag(ScmProviderRepository repository, String name);

    List<Reference> listTags(ScmProviderRepository repository, TagListOptions opts);

    Commit findCommit(ScmProviderRepository repository, String ref);

    List<Commit> listCommits(ScmProviderRepository repository, CommitListOptions opts);

    List<Change> listChanges(ScmProviderRepository repository, String ref, ListOptions opts);

    List<Change> compareChanges(ScmProviderRepository repository, String source, String target, ListOptions opts);
}
