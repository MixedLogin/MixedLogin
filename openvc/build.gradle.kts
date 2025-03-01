plugins {
    kotlin("jvm") version "2.0.21" // max version of mckotlin-velocity

    java
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
    implementation(fileTree("libs") { include("*.jar") })
    implementation("io.netty:netty-all:4.1.63.Final")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.19.0")
    compileOnly("com.google.inject:guice:4.2.3")
    compileOnly("com.google.guava:guava:33.4.0-jre")

    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
