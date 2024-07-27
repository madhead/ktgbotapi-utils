import gradle.kotlin.dsl.accessors._9d6accdeac6876c73060866945fb6d8c.kotlin
import org.gradle.api.internal.artifacts.configurations.ConfigurationInternal
import org.gradle.api.publish.internal.component.ConfigurationVariantMapping
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinSoftwareComponentWithCoordinatesAndPublication
import java.lang.System.getenv as env

plugins {
    id("dokka-convention")
    id("maven-publish")
    signing
}

tasks {
    val dokkaHtml by getting(DokkaTask::class)

    register<Jar>("javadoc") {
        dependsOn(dokkaHtml)
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            val javadoc by tasks.existing

            artifact(javadoc)

            pom {
                name = "ktgbotapi-utils :: ${project.name}"
                description = project.description
                url = "https://github.com/madhead/ktgbotapi-utils"

                licenses {
                    license {
                        name = "MIT"
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
        }

        repositories {
            if (env("GITHUB_ACTIONS") != null) {
                // TODO: add sha / run ID to the version
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/${env("GITHUB_REPOSITORY")}")
                    credentials {
                        username = env("GITHUB_ACTOR")
                        password = env("GITHUB_TOKEN")
                    }
                }
            }
        }
    }
}

if (env("CI") != null) {
    signing {
        useInMemoryPgpKeys(env("SIGNING_KEY"), env("SIGNING_PASSWORD"))
        sign(publishing.publications)
    }

    tasks.withType<AbstractPublishToMaven> {
        dependsOn(tasks.withType<Sign>())
    }
}
