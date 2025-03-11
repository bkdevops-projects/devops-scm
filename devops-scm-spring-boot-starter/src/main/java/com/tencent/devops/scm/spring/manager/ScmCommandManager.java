package com.tencent.devops.scm.spring.manager;

import com.tencent.devops.scm.api.ScmCommand;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.spring.properties.ScmProviderProperties;
import java.util.List;

public class ScmCommandManager {

    private final List<ScmCommandFactory> commandFactories;

    public ScmCommandManager(List<ScmCommandFactory> commandFactories) {
        this.commandFactories = commandFactories;
    }

    public ScmCommand build(ScmProviderProperties properties) {
        for (ScmCommandFactory factory : commandFactories) {
            if (factory.support(properties)) {
                return factory.build(properties);
            }
        }
        throw new NoSuchScmProviderException(properties.getProviderCode());
    }

    public void remoteInfo(ScmProviderProperties properties, ScmProviderRepository repository) {
        build(properties).remoteInfo(repository);
    }
}
