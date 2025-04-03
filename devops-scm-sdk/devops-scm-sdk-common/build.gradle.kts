dependencies {
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.squareup.okhttp3:okhttp")
    api("commons-io:commons-io")
    api("org.apache.commons:commons-lang3")
    api("org.slf4j:slf4j-api")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
