package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.gitlab.auth.GitlabAuthProvider;
import lombok.Getter;

public class GitlabApi {
    @Getter
    private final GitlabApiClient client;
    private volatile GitlabProjectsApi projectsApi;
    private volatile GitlabProjectHooksApi projectHooksApi;
    private volatile GitlabBranchesApi branchesApi;
    private volatile GitlabTagsApi tagsApi;
    private volatile GitlabCommitsApi commitsApi;
    private volatile GitlabMergeRequestsApi mergeRequestsApi;
    private volatile GitlabIssuesApi issuesApi;
    private volatile GitlabNotesApi notesApi;
    private volatile GitlabRepositoryFilesApi repositoryFilesApi;
    private volatile GitlabUsersApi usersApi;
    private volatile GitlabCommitStatusesApi commitStatusesApi;

    public GitlabApi(String apiUrl, ScmConnector connector, GitlabAuthProvider authProvider) {
        this(new GitlabApiClient(apiUrl, connector, authProvider));
    }

    public GitlabApi(GitlabApiClient client) {
        this.client = client;
    }

    Requester createRequest() {
        Requester requester = new Requester(client);
        requester.setIteratorFactory(GitlabPagedIterator::create);
        return requester;
    }

    public GitlabProjectsApi getProjectsApi() {
        if (projectsApi == null) {
            synchronized (this) {
                if (projectsApi == null) {
                    projectsApi = new GitlabProjectsApi(this);
                }
            }
        }
        return projectsApi;
    }

    public GitlabProjectHooksApi getProjectHooksApi() {
        if (projectHooksApi == null) {
            synchronized (this) {
                if (projectHooksApi == null) {
                    projectHooksApi = new GitlabProjectHooksApi(this);
                }
            }
        }
        return projectHooksApi;
    }

    public GitlabBranchesApi getBranchesApi() {
        if (branchesApi == null) {
            synchronized (this) {
                if (branchesApi == null) {
                    branchesApi = new GitlabBranchesApi(this);
                }
            }
        }
        return branchesApi;
    }

    public GitlabTagsApi getTagsApi() {
        if (tagsApi == null) {
            synchronized (this) {
                if (tagsApi == null) {
                    tagsApi = new GitlabTagsApi(this);
                }
            }
        }
        return tagsApi;
    }

    public GitlabCommitsApi getCommitsApi() {
        if (commitsApi == null) {
            synchronized (this) {
                if (commitsApi == null) {
                    commitsApi = new GitlabCommitsApi(this);
                }
            }
        }
        return commitsApi;
    }

    public GitlabMergeRequestsApi getMergeRequestsApi() {
        if (mergeRequestsApi == null) {
            synchronized (this) {
                if (mergeRequestsApi == null) {
                    mergeRequestsApi = new GitlabMergeRequestsApi(this);
                }
            }
        }
        return mergeRequestsApi;
    }

    public GitlabIssuesApi getIssuesApi() {
        if (issuesApi == null) {
            synchronized (this) {
                if (issuesApi == null) {
                    issuesApi = new GitlabIssuesApi(this);
                }
            }
        }
        return issuesApi;
    }

    public GitlabNotesApi getNotesApi() {
        if (notesApi == null) {
            synchronized (this) {
                if (notesApi == null) {
                    notesApi = new GitlabNotesApi(this);
                }
            }
        }
        return notesApi;
    }

    public GitlabRepositoryFilesApi getRepositoryFilesApi() {
        if (repositoryFilesApi == null) {
            synchronized (this) {
                if (repositoryFilesApi == null) {
                    repositoryFilesApi = new GitlabRepositoryFilesApi(this);
                }
            }
        }
        return repositoryFilesApi;
    }

    public GitlabUsersApi getUsersApi() {
        if (usersApi == null) {
            synchronized (this) {
                if (usersApi == null) {
                    usersApi = new GitlabUsersApi(this);
                }
            }
        }
        return usersApi;
    }

    public GitlabCommitStatusesApi getCommitStatusesApi() {
        if (commitStatusesApi == null) {
            synchronized (this) {
                if (commitStatusesApi == null) {
                    commitStatusesApi = new GitlabCommitStatusesApi(this);
                }
            }
        }
        return commitStatusesApi;
    }
}
