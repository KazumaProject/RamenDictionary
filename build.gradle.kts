plugins {
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.serialization") version "1.9.23"
    id("org.jetbrains.kotlin.kapt") version "1.8.22"
    application
}

group = "com.kazumaproject"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4") // Keep only this version
    implementation("io.ktor:ktor-server-core:2.0.3")
    implementation("io.ktor:ktor-server-netty:2.0.3")
    implementation("io.ktor:ktor-server-content-negotiation:2.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.3")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    testImplementation("io.ktor:ktor-server-tests:2.0.3")
    implementation("com.google.dagger:dagger:2.50")
    kapt("com.google.dagger:dagger-compiler:2.50")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
application {
    mainClass.set("MainKt")
}
kapt {
    correctErrorTypes = true
}