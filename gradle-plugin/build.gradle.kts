plugins {
    id("org.jetbrains.kotlin.jvm")
    `maven-publish`
    signing

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

val maven by publishing.publications.creatingDecompileCrasher(project)
publishing.repositories.addOSSRH(project)
signing.sign(maven)
