plugins {
    id("kotlin-convention")
    id("dokka-convention")
    id("release-convention")
    id("publish-convention")
}

dependencies {
    commonMainApi(libs.ktgbotapi.core)
    commonMainImplementation(libs.kslog)

    jvmTestImplementation(libs.ktgbotapi.core)
    jvmTestImplementation(platform(libs.junit.bom))
    jvmTestImplementation(libs.junit.jupiter)
    jvmTestImplementation(libs.kotlinx.coroutines.test)
    jvmTestRuntimeOnly(libs.junit.platform.launcher)
}

tasks {
    jvmTest {
        useJUnitPlatform()
    }
}
