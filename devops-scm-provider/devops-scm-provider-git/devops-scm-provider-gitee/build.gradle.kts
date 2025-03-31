dependencies {
    api(project(":devops-scm-api"))
    api(project(":devops-scm-provider:devops-scm-provider-git:devops-scm-provider-git-common"))
    api(project(":devops-scm-sdk:devops-scm-sdk-gitee"))

    api("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    api("org.apache.commons:commons-lang3")

    testImplementation("ch.qos.logback:logback-core")
    testImplementation("ch.qos.logback:logback-classic")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")
}
