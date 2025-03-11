package com.tencent.devops.scm.provider.git.command;

import com.tencent.devops.scm.api.ScmCommand;
import com.tencent.devops.scm.api.exception.ScmApiException;
import com.tencent.devops.scm.api.pojo.auth.AccessTokenScmAuth;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth;
import com.tencent.devops.scm.api.pojo.auth.PrivateTokenScmAuth;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JGitScmCommand implements ScmCommand {

    private static final Logger logger = LoggerFactory.getLogger(JGitScmCommand.class);

    @Override
    public void remoteInfo(ScmProviderRepository repository) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        String url = repo.getUrl();
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("url cannot be empty");
        }
        try {
            IScmAuth auth = repo.getAuth();
            if (auth instanceof AccessTokenScmAuth || auth instanceof PersonalAccessTokenScmAuth
                    || auth instanceof PrivateTokenScmAuth) {
                return;
            }
            CredentialsProvider credentialsProvider = JGitUtils.getCredentials(repo);
            LsRemoteCommand command = Git.lsRemoteRepository()
                    .setRemote(url)
                    .setCredentialsProvider(credentialsProvider)
                    .setTransportConfigCallback(new JGitTransportConfigCallback(repo));
            command.call();
        } catch (Exception exception) {
            logger.error("Failed to get git remote info", exception);
            throw new ScmApiException(exception);
        }
    }
}
