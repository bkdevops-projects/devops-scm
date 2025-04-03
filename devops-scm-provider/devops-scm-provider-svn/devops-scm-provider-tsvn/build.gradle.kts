dependencies {
    api(project(":devops-scm-api"))
    api(project(":devops-scm-provider:devops-scm-provider-svn:devops-scm-provider-svn-common"))
    api("org.apache.commons:commons-lang3")

    testImplementation("ch.qos.logback:logback-core")
    testImplementation("ch.qos.logback:logback-classic")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")
}
