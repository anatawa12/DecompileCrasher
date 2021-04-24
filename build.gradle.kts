plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.32"
    id("com.gradle.plugin-publish") version "0.14.0" apply false
    application
    `maven-publish`
    signing
}

group = "com.anatawa12.decompileCrasher"
version = "1.2.2"

application {
    mainClass.set("com.anatawa12.decompileCrasher.core.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.ow2.asm:asm:5.0.3")
    implementation("org.ow2.asm:asm-commons:5.0.3")
    testImplementation("junit:junit:4.12")
}

java {
    withJavadocJar()
    withSourcesJar()
}

val maven by publishing.publications.creatingDecompileCrasher(project)
publishing.repositories.addOSSRH(project)
signing.sign(maven)
