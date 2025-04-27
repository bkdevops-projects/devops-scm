dependencies {
    api(project(":devops-scm-api"))
    api(project(":devops-scm-provider:devops-scm-provider-git:devops-scm-provider-git-common"))
    implementation("com.gitee.sdk:gitee5j:1.1.0")
    api(project(":devops-scm-sdk:devops-scm-sdk-common"))

    api("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    api("org.apache.commons:commons-lang3")

    testImplementation("ch.qos.logback:logback-core")
    testImplementation("ch.qos.logback:logback-classic")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")
}
