package com.tencent.devops.scm.spring.manager

import com.tencent.devops.scm.api.FileService
import com.tencent.devops.scm.api.IssueService
import com.tencent.devops.scm.api.PullRequestService
import com.tencent.devops.scm.api.RefService
import com.tencent.devops.scm.api.RepositoryService
import com.tencent.devops.scm.api.ScmCommand
import com.tencent.devops.scm.api.ScmProvider
import com.tencent.devops.scm.api.TokenService
import com.tencent.devops.scm.api.UserService
import com.tencent.devops.scm.api.WebhookEnricher
import com.tencent.devops.scm.api.WebhookParser
import com.tencent.devops.scm.spring.properties.ScmProviderProperties

class ScmProviderManager(
    private val providerFactories: List<ScmProviderFactory>
) {

    fun build(properties: ScmProviderProperties): ScmProvider = build(properties, false)

    fun build(properties: ScmProviderProperties, useTokenApi: Boolean): ScmProvider {
        return providerFactories.firstOrNull { it.support(properties) }
            ?.build(properties, useTokenApi)
            ?: throw NoSuchScmProviderException(properties.providerCode!!)
    }

    fun repositories(properties: ScmProviderProperties): RepositoryService = 
        build(properties).repositories()

    fun refs(properties: ScmProviderProperties): RefService = 
        build(properties).refs()

    fun issues(properties: ScmProviderProperties): IssueService = 
        build(properties).issues()

    fun users(properties: ScmProviderProperties): UserService = 
        build(properties).users()

    fun files(properties: ScmProviderProperties): FileService = 
        build(properties).files()

    fun webhookParser(properties: ScmProviderProperties): WebhookParser = 
        build(properties).webhookParser()

    fun webhookEnricher(properties: ScmProviderProperties): WebhookEnricher = 
        build(properties).webhookEnricher()

    fun pullRequests(properties: ScmProviderProperties): PullRequestService = 
        build(properties).pullRequests()

    fun token(properties: ScmProviderProperties): TokenService = 
        build(properties, true).token()

    fun command(properties: ScmProviderProperties): ScmCommand = 
        build(properties).command()
}