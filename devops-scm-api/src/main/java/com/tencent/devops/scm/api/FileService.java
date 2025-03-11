package com.tencent.devops.scm.api;

import com.tencent.devops.scm.api.pojo.Content;
import com.tencent.devops.scm.api.pojo.ContentInput;
import com.tencent.devops.scm.api.pojo.Tree;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import java.util.List;

public interface FileService {

    Content find(ScmProviderRepository repository, String path, String ref);

    void create(ScmProviderRepository repository, String path, ContentInput input);

    void update(ScmProviderRepository repository, String path, ContentInput input);

    List<Tree> listTree(ScmProviderRepository repository, String path, String ref, Boolean recursive);
}
