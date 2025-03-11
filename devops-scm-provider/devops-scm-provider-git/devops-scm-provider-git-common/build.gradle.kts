dependencies {
    api("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    api("org.eclipse.jgit:org.eclipse.jgit")
    api("org.eclipse.jgit:org.eclipse.jgit.ssh.jsch")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    api(project(":devops-scm-api"))
}
