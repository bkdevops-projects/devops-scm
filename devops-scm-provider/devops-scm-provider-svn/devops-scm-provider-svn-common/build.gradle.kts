dependencies {
    api("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    api(project(":devops-scm-api"))

    api("org.tmatesoft.svnkit:svnkit")
}
