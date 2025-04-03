dependencies {
    api(project(":devops-scm-sdk:devops-scm-sdk-common"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("ch.qos.logback:logback-core")
    testImplementation("ch.qos.logback:logback-classic")
    testImplementation("org.mockito:mockito-core")
}
