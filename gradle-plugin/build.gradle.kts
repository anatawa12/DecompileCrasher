plugins {
    id("org.jetbrains.kotlin.jvm")
    `maven-publish`
    signing
    `java-gradle-plugin`
}

group = project(":").group
version = project(":").version

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation(project(":"))
}

gradlePlugin {
    plugins {
        create("decompileCrasher") {
            id = "com.anatawa12.tools.decompileCrasher"
            implementationClass = "com.anatawa12.tools.decompileCrasher.gradle.PluginMain"
        }
    }
}

val maven by publishing.publications.creatingDecompileCrasher(project)
publishing.repositories.addOSSRH(project)
signing.sign(maven)
