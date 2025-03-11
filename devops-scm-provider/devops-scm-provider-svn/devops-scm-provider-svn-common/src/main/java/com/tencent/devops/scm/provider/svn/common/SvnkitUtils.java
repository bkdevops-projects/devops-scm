package com.tencent.devops.scm.provider.svn.common;

import com.tencent.devops.scm.api.enums.ContentKind;
import com.tencent.devops.scm.api.pojo.Tree;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.auth.SshPrivateKeyScmAuth;
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth;
import com.tencent.devops.scm.api.pojo.auth.TokenUserPassScmAuth;
import com.tencent.devops.scm.api.pojo.auth.UserPassScmAuth;
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.SVNAuthentication;
import org.tmatesoft.svn.core.auth.SVNPasswordAuthentication;
import org.tmatesoft.svn.core.auth.SVNSSHAuthentication;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.DefaultSVNRepositoryPool;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnkitUtils {

    public static SVNRepository openRepo(SvnScmProviderRepository providerRepository) throws SVNException {
        SVNURL svnURL = SVNURL.parseURIEncoded(providerRepository.getUrl());

        SVNAuthentication[] authentications = {createAuthentication(svnURL, providerRepository)};
        BasicAuthenticationManager authManager = new TmatesoftBasicAuthenticationManager(authentications);
        DefaultSVNRepositoryPool options = new DefaultSVNRepositoryPool(authManager,
                SVNWCUtil.createDefaultOptions(true), 30 * 1000L, true);

        SVNRepository repository = SVNRepositoryFactory.create(svnURL, options);
        repository.setAuthenticationManager(authManager);
        return repository;
    }

    public static void closeRepo(SVNRepository repository) {
        if (repository != null) {
            repository.closeSession();
        }
    }

    public static SVNAuthentication createAuthentication(SVNURL svnURL, SvnScmProviderRepository providerRepository) {
        IScmAuth auth = providerRepository.getAuth();
        SVNAuthentication authentication;
        if (auth instanceof UserPassScmAuth) {
            UserPassScmAuth userPassScmAuth = (UserPassScmAuth) auth;
            authentication = SVNPasswordAuthentication.newInstance(
                    userPassScmAuth.getUsername(),
                    userPassScmAuth.getPassword().toCharArray(),
                    false,
                    svnURL,
                    false
            );
        } else if (auth instanceof TokenUserPassScmAuth) {
            TokenUserPassScmAuth tokenUserPassScmAuth = (TokenUserPassScmAuth) auth;
            authentication = SVNPasswordAuthentication.newInstance(
                    tokenUserPassScmAuth.getUsername(),
                    tokenUserPassScmAuth.getPassword().toCharArray(),
                    false,
                    svnURL,
                    false
            );
        } else if (auth instanceof SshPrivateKeyScmAuth) {
            SshPrivateKeyScmAuth sshPrivateKeyScmAuth = (SshPrivateKeyScmAuth) auth;
            authentication = SVNSSHAuthentication.newInstance(
                    providerRepository.getUserName(),
                    sshPrivateKeyScmAuth.getPrivateKey().toCharArray(),
                    sshPrivateKeyScmAuth.getPassphrase() == null ? null
                            : sshPrivateKeyScmAuth.getPassphrase().toCharArray(),
                    22,
                    false,
                    svnURL,
                    false
            );
        } else if (auth instanceof TokenSshPrivateKeyScmAuth) {
            TokenSshPrivateKeyScmAuth tokenSshPrivateKeyScmAuth = (TokenSshPrivateKeyScmAuth) auth;
            authentication = SVNSSHAuthentication.newInstance(
                    providerRepository.getUserName(),
                    tokenSshPrivateKeyScmAuth.getPrivateKey().toCharArray(),
                    tokenSshPrivateKeyScmAuth.getPassphrase() == null ? null
                            : tokenSshPrivateKeyScmAuth.getPassphrase().toCharArray(),
                    22,
                    false,
                    svnURL,
                    false
            );
        } else {
            throw new UnsupportedOperationException("not support svn auth type");
        }
        return authentication;
    }

    public static List<Tree> listFiles(SVNRepository svnRepository, String path, long revision, Boolean recursive)
            throws SVNException {
        List<Tree> trees = new ArrayList<>();
        Set<SVNDirEntry> dirEntries = new HashSet<>();
        svnRepository.getDir(path, revision, false, dirEntries);
        for (SVNDirEntry entry : dirEntries) {
            String filePath = "".equals(path) ? entry.getName() : path + "/" + entry.getName();
            if (entry.getKind() == SVNNodeKind.DIR) {
                Tree tree = Tree.builder()
                        .path(filePath)
                        .sha(String.valueOf(entry.getRevision()))
                        .kind(ContentKind.DIRECTORY)
                        .blobId(String.valueOf(entry.getRevision()))
                        .build();
                trees.add(tree);

                if (recursive) {
                    List<Tree> subTrees = listFiles(svnRepository, filePath, entry.getRevision(), true);
                    trees.addAll(subTrees);
                }
            } else {
                Tree tree = Tree.builder()
                        .path(filePath)
                        .sha(String.valueOf(entry.getRevision()))
                        .kind(ContentKind.FILE)
                        .blobId(String.valueOf(entry.getRevision()))
                        .build();
                trees.add(tree);
            }
        }
        return trees;
    }
}
