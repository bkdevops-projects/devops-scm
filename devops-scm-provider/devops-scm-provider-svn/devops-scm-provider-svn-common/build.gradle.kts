dependencies {
    api(project(":devops-scm-api"))
    api("org.tmatesoft.svnkit:svnkit")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    api(project(":devops-scm-provider:devops-scm-provider-common"))
}
