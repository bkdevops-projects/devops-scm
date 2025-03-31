dependencies {
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-configuration-processor")

    api(project(":devops-scm-provider:devops-scm-provider-git:devops-scm-provider-git-common"))
    api(project(":devops-scm-provider:devops-scm-provider-git:devops-scm-provider-tgit"))
    api(project(":devops-scm-provider:devops-scm-provider-git:devops-scm-provider-gitee"))
    api(project(":devops-scm-provider:devops-scm-provider-svn:devops-scm-provider-svn-common"))
    api(project(":devops-scm-provider:devops-scm-provider-svn:devops-scm-provider-tsvn"))
    compileOnly("io.micrometer:micrometer-core")

    api("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
