package com.tencent.devops.scm.provider.svn.common;

import com.tencent.devops.scm.api.FileService;
import com.tencent.devops.scm.api.enums.ContentKind;
import com.tencent.devops.scm.api.exception.ScmApiException;
import com.tencent.devops.scm.api.pojo.Content;
import com.tencent.devops.scm.api.pojo.ContentInput;
import com.tencent.devops.scm.api.pojo.Tree;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.SVNRepository;

public class SvnFileService implements FileService {

    @Override
    public Content find(ScmProviderRepository repository, String path, String ref) {
        SvnScmProviderRepository providerRepository = (SvnScmProviderRepository) repository;
        try {
            OutputStream bos = new ByteArrayOutputStream();
            SVNRepository svnRepository = SvnkitUtils.openRepo(providerRepository);
            svnRepository.getFile(path, Long.parseLong(ref), new SVNProperties(), bos);
            return Content.builder()
                    .path(path)
                    .sha(ref)
                    .content(bos.toString())
                    .blobId(ref)
                    .build();
        } catch (SVNException e) {
            throw new ScmApiException(e);
        }
    }

    @Override
    public void create(ScmProviderRepository repository, String path, ContentInput input) {
    }

    @Override
    public void update(ScmProviderRepository repository, String path, ContentInput input) {

    }

    @Override
    public List<Tree> listTree(ScmProviderRepository repository, String path, String ref, Boolean recursive) {
        SvnScmProviderRepository providerRepository = (SvnScmProviderRepository) repository;
        try {
            long revision = Long.parseLong(ref);
            SVNRepository svnRepository = SvnkitUtils.openRepo(providerRepository);
            SVNNodeKind svnNodeKind = svnRepository.checkPath(path, revision);
            List<Tree> trees = new ArrayList<>();
            if (svnNodeKind == SVNNodeKind.DIR) {
                trees.addAll(SvnkitUtils.listFiles(svnRepository, path, revision, recursive));
            } else {
                Tree tree = Tree.builder()
                        .path(path)
                        .sha(ref)
                        .kind(ContentKind.FILE)
                        .blobId(ref)
                        .build();
                trees.add(tree);
            }
            return trees;
        } catch (SVNException e) {
            throw new ScmApiException(e);
        }
    }
}
