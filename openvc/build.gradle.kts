plugins {
    kotlin("jvm") version "2.0.21" // max version of mckotlin-velocity
    java
//    shadow
    alias(libs.plugins.shadow)
}

group = "fun.iiii.mixedlogin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation("org.spongepowered:configurate-extra-kotlin:4.2.0")
    compileOnly("org.spongepowered:configurate-hocon:4.2.0")
    compileOnly(fileTree("libs") { include("*.jar") })
    compileOnly("io.netty:netty-all:4.1.63.Final")
    compileOnly("com.google.code.gson:gson:2.8.9")
    compileOnly("org.apache.logging.log4j:log4j-api:2.14.1")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.19.0")
    compileOnly("com.google.inject:guice:4.2.3")
    compileOnly("com.google.guava:guava:33.4.0-jre")

    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    shadowJar {
        archiveBaseName.set("MixedLogin-OpenVelocity")
        archiveClassifier.set("")
        dependencies{
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
            exclude(dependency("org.jetbrains:annotations"))
//            extra-kotlin
            include(dependency("org.spongepowered:configurate-extra-kotlin"))
        }
    }
    build {
        dependsOn(shadowJar)
    }
}
