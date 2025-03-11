package com.tencent.devops.scm.provider.git.command;

import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.auth.TokenUserPassScmAuth;
import com.tencent.devops.scm.api.pojo.auth.UserPassScmAuth;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class JGitUtils {

    public static CredentialsProvider getCredentials(GitScmProviderRepository repository) {
        IScmAuth auth = repository.getAuth();
        if (auth instanceof UserPassScmAuth) {
            UserPassScmAuth userPassScmAuth = (UserPassScmAuth) auth;
            return new UsernamePasswordCredentialsProvider(userPassScmAuth.getUsername(),
                    userPassScmAuth.getPassword());
        }
        if (auth instanceof TokenUserPassScmAuth) {
            TokenUserPassScmAuth tokenUserPassScmAuth = (TokenUserPassScmAuth) auth;
            return new UsernamePasswordCredentialsProvider(tokenUserPassScmAuth.getUsername(),
                    tokenUserPassScmAuth.getPassword());
        }
        return null;
    }
}
