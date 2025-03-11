package com.tencent.devops.scm.provider.git.command;

import com.tencent.devops.scm.api.ScmCommand;
import com.tencent.devops.scm.api.ScmProvider;

public abstract class GitScmProvider implements ScmProvider {

    @Override
    public ScmCommand command() {
        return new JGitScmCommand();
    }
}
