import util.MavenUtil

plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

nexusPublishing {
    repositories {

        sonatype {
            stagingProfileId.set("com.tencent.bk")
            nexusUrl.set(uri(MavenUtil.getUrl(project)))
            snapshotRepositoryUrl.set(uri(MavenUtil.getUrl(project)))

            username.set(MavenUtil.getUserName(project))
            password.set(MavenUtil.getPassword(project))
        }
    }
}
