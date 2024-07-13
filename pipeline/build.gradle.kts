plugins {
    id("kotlin-convention")
}

dependencies {
    commonMainCompileOnly(libs.ktgbotapi.core)
    commonMainImplementation(libs.kslog)
}
