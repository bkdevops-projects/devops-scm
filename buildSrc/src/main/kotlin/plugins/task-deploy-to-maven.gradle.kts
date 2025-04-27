/*
 * Tencent is pleased to support the open source community by making BK-CI 蓝鲸持续集成平台 available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-CI 蓝鲸持续集成平台 is licensed under the MIT license.
 *
 * A copy of the MIT License is included in this file.
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import java.net.URI

plugins {
    `maven-publish`
    signing
    java
}

val sourceJar = tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(project.the<SourceSetContainer>()["main"].allSource)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    val javadoc = tasks.getByName("javadoc")
    dependsOn(javadoc)
    archiveClassifier.set("javadoc")
    from(javadoc)
}

tasks.getByName<Javadoc>("javadoc") {
    description = "javadoc for devops-scm"
    val options: StandardJavadocDocletOptions = options as StandardJavadocDocletOptions
    options.memberLevel = JavadocMemberLevel.PROTECTED
    options.header = project.name
    options.isAuthor = true
    options.isVersion = true
    // 不检查：非标的javadoc注解不报错
    options.addStringOption("Xdoclint:none", "-quiet")
    options.addStringOption("charset", "UTF-8")
    options.encoding = "UTF-8"
    options.charSet = "UTF-8"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            artifact(sourceJar)
            artifact(javadocJar)

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name.set("devops-scm")
                description.set("Tencent BlueKing DevOps Scm")
                url.set("https://github.com/bkdevops-projects/devops-scm")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/bkdevops-projects/devops-scm.git")
                        distribution.set("repo")
                        comments.set("A business-friendly OSS license")
                    }
                }

                developers {
                    developer {
                        name.set("bk-ci")
                        email.set("devops@tencent.com")
                        roles.set(listOf("Manager"))
                        url.set("https://bk.tencent.com")
                    }
                }

                scm {
                    url.set("https://github.com/bkdevops-projects/devops-scm")
                    connection.set("scm:git:https://github.com/bkdevops-projects/devops-scm.git")
                    developerConnection.set("scm:git:git@github.com:bkdevops-projects/devops-scm.git")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

tasks.getByName("publish") {
    onlyIf {
        project.the<SourceSetContainer>()["main"].allSource.files.isNotEmpty()
        // 演示项目不打包
        !project.name.contains("devops-scm-provider-gitee-simple")
    }
}

tasks.getByName("generateMetadataFileForMavenJavaPublication") {
    onlyIf {
        project.the<SourceSetContainer>()["main"].allSource.files.isNotEmpty()
    }
}

val shouldPublish = project.the<SourceSetContainer>()["main"].allSource.files.isNotEmpty()

tasks.forEach {
    if (it.group == "publish") {
        it.onlyIf { shouldPublish }
    }
}
