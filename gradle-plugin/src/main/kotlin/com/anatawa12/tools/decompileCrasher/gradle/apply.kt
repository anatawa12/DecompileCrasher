package com.anatawa12.tools.decompileCrasher.gradle

import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import java.io.File

/**
 * Created by anatawa12 on 2018/11/14.
 */
fun Project.apply() {
    apply(mapOf("plugin" to "java"))

    val jar = tasks.getByName("jar") as Jar
    val build = tasks.getByName("build")

    val obfuscationJar = tasks.create("obfuscationJar", ObfuscationTask::class.java).apply {
        jarTask = jar
        destinationDir = File(buildDir, "libs")
        dependsOn(jar)
    }

    build.dependsOn(obfuscationJar)
}