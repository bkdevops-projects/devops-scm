import com.tencent.devops.scm.api.RepositoryService
import com.tencent.devops.scm.api.enums.StatusState
import com.tencent.devops.scm.api.pojo.Hook
import com.tencent.devops.scm.api.pojo.HookEvents
import com.tencent.devops.scm.api.pojo.HookInput
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.Perm
import com.tencent.devops.scm.api.pojo.RepoListOptions
import com.tencent.devops.scm.api.pojo.Status
import com.tencent.devops.scm.api.pojo.StatusInput
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.provider.git.tgit.TGitApiTemplate
import com.tencent.devops.scm.provider.git.tgit.TGitObjectConverter
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory
import com.tencent.devops.scm.sdk.tgit.enums.TGitAccessLevel
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommitStatus
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMember
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProject
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProjectHook

/**
 * TGit 仓库服务实现类
 * @property apiFactory TGit API工厂
 */
class TGitRepositoryService(
    private val apiFactory: TGitApiFactory
) : RepositoryService {

    /**
     * 查找仓库
     * @param repository 代码仓库信息
     * @return 仓库详情
     */
    override fun find(repository: ScmProviderRepository): GitScmServerRepository {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val project = tGitApi.projectApi.getProject(repo.projectIdOrPath)
            TGitObjectConverter.convertRepository(project)
        }
    }

    /**
     * 查找权限
     * @param repository 代码仓库信息
     * @param username 用户名
     * @return 权限信息
     */
    override fun findPerms(repository: ScmProviderRepository, username: String): Perm {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            try {
                val member = tGitApi.projectApi.getMember(repo.projectIdOrPath, username)
                if (member == null) {
                    Perm.builder().pull(true).push(false).admin(false).build()
                } else {
                    Perm.builder()
                            .pull(true)
                            .push(canPush(member.accessLevel))
                            .admin(canAdmin(member.accessLevel))
                            .build()
                }
            } catch (e: Exception) {
                Perm.builder().pull(false).push(false).admin(false).build()
            }
        }
    }

    /**
     * 获取仓库列表
     * @param auth 认证信息
     * @param opts 列表查询选项
     * @return 仓库列表
     */
    override fun list(auth: IScmAuth, opts: RepoListOptions): List<ScmServerRepository> {
        return TGitApiTemplate.execute(auth, apiFactory) { tGitApi ->
            tGitApi.projectApi.getProjects(opts.repoName, opts.page, opts.pageSize)
                    .map { TGitObjectConverter.convertRepository(it) }
        }
    }

    /**
     * 获取钩子列表
     * @param repository 代码仓库信息
     * @param opts 列表查询选项
     * @return 钩子列表
     */
    override fun listHooks(repository: ScmProviderRepository, opts: ListOptions): List<Hook> {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            tGitApi.projectApi.getHooks(repo.projectIdOrPath, opts.page, opts.pageSize)
                    .map { convertHook(it) }
        }
    }

    /**
     * 创建钩子
     * @param repository 代码仓库信息
     * @param input 钩子输入参数
     * @return 创建的钩子
     */
    override fun createHook(repository: ScmProviderRepository, input: HookInput): Hook {
        if (input.url == null) {
            throw IllegalArgumentException("url can not empty")
        }
        if (input.events == null) {
            throw IllegalArgumentException("events can not empty")
        }
        val tGitProjectHook = convertFromHookInput(input)

        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val hook = tGitApi.projectApi.addHook(repo.projectIdOrPath, tGitProjectHook, input.secret)
            convertHook(hook)
        }
    }

    /**
     * 更新钩子
     * @param repository 代码仓库信息
     * @param hookId 钩子ID
     * @param input 钩子输入参数
     * @return 更新后的钩子
     */
    override fun updateHook(repository: ScmProviderRepository, hookId: Long, input: HookInput): Hook {
        val tGitProjectHook = convertFromHookInput(input)

        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val hook = tGitApi.projectApi.updateHook(repo.projectIdOrPath, hookId, tGitProjectHook, input.secret)
            convertHook(hook)
        }
    }

    /**
     * 删除钩子
     * @param repository 代码仓库信息
     * @param hookId 钩子ID
     */
    override fun deleteHook(repository: ScmProviderRepository, hookId: Long) {
        TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            tGitApi.projectApi.deleteHook(repo.projectIdOrPath, hookId)
        }
    }

    /**
     * 获取钩子
     * @param repository 代码仓库信息
     * @param hookId 钩子ID
     * @return 钩子详情
     */
    override fun getHook(repository: ScmProviderRepository, hookId: Long): Hook {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val hook = tGitApi.projectApi.getHook(repo.projectIdOrPath, hookId)
            convertHook(hook)
        }
    }

    /**
     * 获取状态列表
     * @param repository 代码仓库信息
     * @param ref 引用
     * @param opts 列表查询选项
     * @return 状态列表
     */
    override fun listStatus(repository: ScmProviderRepository, ref: String, opts: ListOptions): List<Status> {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            tGitApi.commitsApi.getCommitStatuses(repo.projectIdOrPath, ref, opts.page, opts.pageSize)
                    .map { convertStatus(it) }
        }
    }

    /**
     * 创建状态
     * @param repository 代码仓库信息
     * @param ref 引用
     * @param input 状态输入参数
     * @return 创建的状态
     */
    override fun createStatus(repository: ScmProviderRepository, ref: String, input: StatusInput): Status? {
        return null
    }

    private fun canPush(accessLevel: TGitAccessLevel): Boolean {
        return accessLevel.value >= TGitAccessLevel.DEVELOPER.value
    }

    private fun canAdmin(accessLevel: TGitAccessLevel): Boolean {
        return accessLevel.value >= TGitAccessLevel.MASTER.value
    }

    private fun convertHook(from: TGitProjectHook): Hook {
        return Hook.builder()
                .id(from.id)
                .url(from.url)
                .active(true)
                .events(convertEvents(from))
                .build()
    }

    private fun convertEvents(from: TGitProjectHook): HookEvents {
        val builder = HookEvents.builder()
        if (from.pushEvents) {
            builder.push(true)
        }
        if (from.tagPushEvents) {
            builder.tag(true)
        }
        if (from.mergeRequestsEvents) {
            builder.pullRequest(true)
        }
        if (from.issuesEvents) {
            builder.issue(true)
        }
        if (from.noteEvents) {
            builder.issueComment(true)
            builder.pullRequestComment(true)
        }
        if (from.reviewEvents) {
            builder.pullRequestReview(true)
        }
        return builder.build()
    }

    private fun convertFromHookInput(input: HookInput): TGitProjectHook {
        val builder = TGitProjectHook.builder()
        builder.url(input.url)
        val events = input.events
        if (events.push == true) {
            builder.pushEvents(true)
        }
        if (events.tag == true) {
            builder.tagPushEvents(true)
        }
        if (events.pullRequest == true) {
            builder.mergeRequestsEvents(true)
        }
        if (events.issue == true) {
            builder.issuesEvents(true)
        }
        if (enableNoteEvent(events)) {
            builder.noteEvents(true)
        }
        if (events.pullRequestReview == true) {
            builder.reviewEvents(true)
        }
        return builder.build()
    }

    private fun enableNoteEvent(events: HookEvents): Boolean {
        return events.pullRequestComment == true || events.issueComment == true
    }

    private fun convertStatus(from: TGitCommitStatus): Status {
        return Status.builder()
                .state(convertState(from.state))
                .context(from.context)
                .desc(from.description)
                .targetUrl(from.targetUrl)
                .build()
    }

    private fun convertState(from: String): StatusState {
        return when (from) {
            "pending" -> StatusState.PENDING
            "success" -> StatusState.SUCCESS
            "error" -> StatusState.ERROR
            "failure" -> StatusState.FAILURE
            else -> StatusState.UNKNOWN
        }
    }
}
