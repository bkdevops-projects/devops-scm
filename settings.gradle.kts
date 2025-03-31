rootProject.name = "devops-scm"

include(":devops-scm-api")

include(":devops-scm-provider")
include(":devops-scm-provider:devops-scm-provider-git")
include(":devops-scm-provider:devops-scm-provider-git:devops-scm-provider-git-common")
include(":devops-scm-provider:devops-scm-provider-git:devops-scm-provider-tgit")

include(":devops-scm-provider:devops-scm-provider-svn")
include(":devops-scm-provider:devops-scm-provider-svn:devops-scm-provider-svn-common")
include(":devops-scm-provider:devops-scm-provider-svn:devops-scm-provider-tsvn")


include(":devops-scm-sdk")
include(":devops-scm-sdk:devops-scm-sdk-common")
include(":devops-scm-sdk:devops-scm-sdk-tgit")

include(":devops-scm-spring-boot-starter")

include(":devops-scm-test")
include(":devops-scm-sample")
include("devops-scm-provider:devops-scm-provider-git:devops-scm-provider-gitee")
findProject(":devops-scm-provider:devops-scm-provider-git:devops-scm-provider-gitee")?.name = "devops-scm-provider-gitee"
include("devops-scm-sdk:devops-scm-sdk-gitee")
findProject(":devops-scm-sdk:devops-scm-sdk-gitee")?.name = "devops-scm-sdk-gitee"
