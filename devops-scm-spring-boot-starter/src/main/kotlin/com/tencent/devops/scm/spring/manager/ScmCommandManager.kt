package com.tencent.devops.scm.spring.manager

import com.tencent.devops.scm.api.ScmCommand
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.spring.properties.ScmProviderProperties

class ScmCommandManager(
    private val commandFactories: List<ScmCommandFactory>
) {

    fun build(properties: ScmProviderProperties): ScmCommand {
        return commandFactories.firstOrNull { it.support(properties) }
            ?.build(properties)
            ?: throw NoSuchScmProviderException(properties.providerCode!!)
    }

    fun remoteInfo(properties: ScmProviderProperties, repository: ScmProviderRepository) {
        build(properties).remoteInfo(repository)
    }
}
