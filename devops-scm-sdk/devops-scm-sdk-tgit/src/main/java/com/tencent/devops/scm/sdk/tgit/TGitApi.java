package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.tgit.auth.TGitUserPassAuthProvider;
import lombok.Getter;

public class TGitApi {

    @Getter
    private final TGitApiClient client;

    private volatile TGitProjectApi projectApi;
    private volatile TGitRepositoryApi repositoryApi;
    private volatile TGitRepositoryFileApi repositoryFileApi;
    private volatile TGitBranchesApi branchesApi;
    private volatile TGitTagsApi tagsApi;
    private volatile TGitCommitsApi commitsApi;
    private volatile TGitMergeRequestApi mergeRequestApi;
    private volatile TGitIssuesApi issuesApi;
    private volatile TGitNotesApi notesApi;
    private volatile TGitReviewApi reviewApi;
    private volatile TGitUserApi userApi;

    public TGitApi(String apiUrl, ScmConnector connector, HttpAuthProvider authorizationProvider) {
        if (authorizationProvider instanceof TGitUserPassAuthProvider) {
            ((TGitUserPassAuthProvider) authorizationProvider).bind(this);
        }
        this.client = new TGitApiClient(apiUrl, connector, authorizationProvider);
    }

    public TGitApi(TGitApiClient client) {
        this.client = client;
    }

    Requester createRequest() {
        Requester requester = new Requester(client);
        requester.setIteratorFactory(TGitPagedIteratorFactory.iteratorFactory);
        return requester;
    }

    public TGitProjectApi getProjectApi() {
        if (projectApi == null) {
            synchronized (this) {
                if (projectApi == null) {
                    projectApi = new TGitProjectApi(this);
                }
            }
        }
        return projectApi;
    }

    public TGitRepositoryApi getRepositoryApi() {
        if (repositoryApi == null) {
            synchronized (this) {
                if (repositoryApi == null) {
                    repositoryApi = new TGitRepositoryApi(this);
                }
            }
        }
        return repositoryApi;
    }

    public TGitRepositoryFileApi getRepositoryFileApi() {
        if (repositoryFileApi == null) {
            synchronized (this) {
                if (repositoryFileApi == null) {
                    repositoryFileApi = new TGitRepositoryFileApi(this);
                }
            }
        }
        return repositoryFileApi;
    }

    public TGitBranchesApi getBranchesApi() {
        if (branchesApi == null) {
            synchronized (this) {
                if (branchesApi == null) {
                    branchesApi = new TGitBranchesApi(this);
                }
            }
        }
        return branchesApi;
    }

    public TGitTagsApi getTagsApi() {
        if (tagsApi == null) {
            synchronized (this) {
                if (tagsApi == null) {
                    tagsApi = new TGitTagsApi(this);
                }
            }
        }
        return tagsApi;
    }

    public TGitCommitsApi getCommitsApi() {
        if (commitsApi == null) {
            synchronized (this) {
                if (commitsApi == null) {
                    commitsApi = new TGitCommitsApi(this);
                }
            }
        }
        return commitsApi;
    }

    public TGitMergeRequestApi getMergeRequestApi() {
        if (mergeRequestApi == null) {
            synchronized (this) {
                if (mergeRequestApi == null) {
                    mergeRequestApi = new TGitMergeRequestApi(this);
                }
            }
        }
        return mergeRequestApi;
    }

    public TGitIssuesApi getIssuesApi() {
        if (issuesApi == null) {
            synchronized (this) {
                if (issuesApi == null) {
                    issuesApi = new TGitIssuesApi(this);
                }
            }
        }
        return issuesApi;
    }

    public TGitNotesApi getNotesApi() {
        if (notesApi == null) {
            synchronized (this) {
                if (notesApi == null) {
                    notesApi = new TGitNotesApi(this);
                }
            }
        }
        return notesApi;
    }

    public TGitReviewApi getReviewApi() {
        if (reviewApi == null) {
            synchronized (this) {
                if (reviewApi == null) {
                    reviewApi = new TGitReviewApi(this);
                }
            }
        }
        return reviewApi;
    }

    public TGitUserApi getUserApi() {
        if (userApi == null) {
            synchronized (this) {
                if (userApi == null) {
                    userApi = new TGitUserApi(this);
                }
            }
        }
        return userApi;
    }
}
