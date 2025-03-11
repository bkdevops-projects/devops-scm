package com.tencent.devops.scm.provider.svn.common;

import com.tencent.devops.scm.api.ScmCommand;
import com.tencent.devops.scm.api.exception.ScmApiException;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

public class SvnScmCommand implements ScmCommand {

    @Override
    public void remoteInfo(ScmProviderRepository repository) {
        SvnScmProviderRepository providerRepository = (SvnScmProviderRepository) repository;
        try {
            SVNRepository svnRepository = SvnkitUtils.openRepo(providerRepository);
            svnRepository.info("", -1);
        } catch (SVNException e) {
            throw new ScmApiException(e);
        }
    }
}
