plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.plugin.shadow)
}

repositories {
    gradlePluginPortal()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
