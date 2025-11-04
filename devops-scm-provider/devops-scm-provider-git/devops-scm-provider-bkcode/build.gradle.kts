dependencies {
    api(project(":devops-scm-api"))
    api(project(":devops-scm-provider:devops-scm-provider-git:devops-scm-provider-git-common"))
    api(project(":devops-scm-sdk:devops-scm-sdk-bkcode"))

    api("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    api("org.apache.commons:commons-lang3")

    testImplementation("ch.qos.logback:logback-core")
    testImplementation("ch.qos.logback:logback-classic")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")
}

// 引入sdk模块的测试json，避免出现重复json
sourceSets {
    test {
        resources {
            setSrcDirs(
                srcDirs.plus(
                    project(":devops-scm-sdk:devops-scm-sdk-bkcode").sourceSets.test.get().resources.srcDirs
                )
            )
        }
    }
}
