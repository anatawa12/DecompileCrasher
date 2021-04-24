plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.gradle.plugin-publish")
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

pluginBundle {
    website = "https://github.com/anatawa12/DecompileCrasher"
    vcsUrl = "https://github.com/anatawa12/DecompileCrasher"
    description = ("A tool to make a Jar Hard to decompile. " +
            "Decompiler will make invalid source code or " +
            "internal error will be thrown")
    tags = mutableListOf("obfuscation", "obfuscator")

    plugins {
        get("decompileCrasher").apply {
            displayName = "DecompileCrasher"
        }
    }
}

val maven by publishing.publications.creatingDecompileCrasher(project)
publishing.repositories.addOSSRH(project)
signing.sign(maven)

tasks.withType<PublishToMavenRepository>().configureEach {
    onlyIf {
        if (repository.name == "mavenCentral") {
            publication.name != "decompileCrasherPluginMarker"
                    && publication.name != "pluginMaven"
        } else {
            true
        }
    }
}
