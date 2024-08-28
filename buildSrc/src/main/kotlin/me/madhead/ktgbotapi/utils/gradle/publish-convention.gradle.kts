import com.vanniktech.maven.publish.SonatypeHost
import me.madhead.ktgbotapi.utils.gradle.build.CI
import java.lang.System.getenv as env

plugins {
//    id("dokka-convention")
    id("com.vanniktech.maven.publish")
    signing
}

//tasks {
//    val dokkaHtml by getting(DokkaTask::class)
//
//    register<Jar>("javadoc") {
//        dependsOn(dokkaHtml)
//        archiveClassifier.set("javadoc")
//        from(dokkaHtml.outputDirectory)
//    }
//}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

    pom {
        name = "ktgbotapi-utils :: ${project.name}"
        description = project.description
        url = "https://github.com/madhead/ktgbotapi-utils"

        licenses {
            license {
                name = "MIT"
                distribution = "repo"
                url = "https://github.com/madhead/ktgbotapi-utils/blob/master/LICENSE"
            }
        }

        developers {
            developer {
                id = "madhead"
                name = "Siarhei Krukau"
                email = "siarhei.krukau@gmail.com"
                url = "https://madhead.me"
            }
        }

        scm {
            url = "https://github.com/madhead/ktgbotapi-utils"
            connection = "scm:git:https://github.com/madhead/ktgbotapi-utils.git"
            developerConnection = "scm:git:git@github.com:madhead/ktgbotapi-utils.git"
        }
    }

    signAllPublications()
}

if (CI) {
    signing {
        useGpgCmd()
//        useInMemoryPgpKeys(env("SIGNING_KEY"), env("SIGNING_PASSWORD"))
        sign(publishing.publications)
    }

    tasks.withType<AbstractPublishToMaven> {
        dependsOn(tasks.withType<Sign>())
    }
}
