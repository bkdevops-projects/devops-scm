dependencies {
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.core:jackson-annotations")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    api("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    api("org.apache.commons:commons-lang3")
    api("org.apache.commons:commons-collections4")
}
