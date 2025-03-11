description = "Tencent BlueKing DevOps Scm"

plugins {
    id("io.spring.dependency-management")
    id("checkstyle")
}

allprojects {
    group = Release.Group
    version = if (System.getProperty("snapshot") == "true") {
        "${Release.Version}-SNAPSHOT"
    } else {
        Release.Version
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "task-deploy-to-maven")
    apply(plugin = "checkstyle")

    dependencyManagement {
        dependencies {
            dependencySet("com.fasterxml.jackson.core:${Versions.jackson}") {
                entry("jackson-core")
                entry("jackson-databind")
                entry("jackson-annotations")
            }
            dependency("org.apache.commons:commons-lang3:${Versions.commonsLang3}")
            dependency("org.apache.commons:commons-collections4:${Versions.commonsCollections}")
            dependency("org.projectlombok:lombok:${Versions.lombok}")
            dependency("com.squareup.okhttp3:okhttp:${Versions.Okhttp}")
            dependency("commons-io:commons-io:${Versions.CommonIo}")
            dependency("org.slf4j:slf4j-api:${Versions.slf4j}")
            dependencySet("org.junit.jupiter:${Versions.Junit}") {
                entry("junit-jupiter-api")
                entry("junit-jupiter-engine")
            }
            dependency("org.mockito:mockito-core:${Versions.mockito}")
            dependencySet("ch.qos.logback:${Versions.logback}") {
                entry("logback-core")
                entry("logback-classic")
            }
            dependencySet("org.eclipse.jgit:${Versions.jgit}") {
                entry("org.eclipse.jgit")
                entry("org.eclipse.jgit.ssh.jsch")
            }
            dependency("org.springframework.boot:spring-boot-autoconfigure:${Versions.springBootVersion}")
            dependency("org.springframework.boot:spring-boot-configuration-processor:${Versions.springBootVersion}")
            dependency("org.tmatesoft.svnkit:svnkit:${Versions.svnKit}")
            dependency("io.micrometer:micrometer-core:${Versions.micrometer}")
        }
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }

    checkstyle {
        toolVersion = "8.45"
        configFile = file("${rootDir}/config/checkstyle/tencent_checkstyle.xml")
    }
}
