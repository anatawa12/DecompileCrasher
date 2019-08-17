package com.anatawa12.tools.decompileCrasher.gradle

import com.anatawa12.decompileCrasher.core.IndyClass
import com.anatawa12.decompileCrasher.core.JarRunner
import com.anatawa12.decompileCrasher.core.MethodFullSignature
import com.anatawa12.decompileCrasher.core.RunnerArguments
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.jvm.tasks.Jar
import java.io.File

/**
 * Created by anatawa12 on 2018/11/14.
 */
@CacheableTask
open class ObfuscationTask() : DefaultTask() {
    var jarTask: Jar? = null

    init {
        group = "build"
        description = "runDecompileCrasher with DecompileCrasher"
    }

    @Input
    var withIndyClass = true

    @Input
    var debug = false

    @Input
    var isRuntimeDebug = false

    @Input
    var solveClassPath: String = IndyClass.default.classPath

    @Input
    var methodSolveMethod: String = IndyClass.default.method

    @Input
    var fieldSolveMethod: String = IndyClass.default.field

    @Input
    var destinationDir: File? = null

    @Input
    var postfix: String? = "obfuscated"

    @Input
    val exclusions: MutableSet<String> = mutableSetOf()

    fun exclusions(vararg exclusions: String) {
        this.exclusions.addAll(exclusions)
    }

    @InputFile
    fun getInputFile(): File? = jarTask?.archivePath

    @OutputFile
    fun getOutputFile(): File = File(destinationDir, archiveName)

    @Input
    private val excludeTargets: MutableList<MethodFullSignature> = mutableListOf()

    fun excludeTarget(name: String) {
        excludeTargets += MethodFullSignature.perse(name)
    }

    @TaskAction
    fun runDecompileCrasher() {
        if (getInputFile() == null) error("input file is null")
        JarRunner.main(RunnerArguments(getInputFile()!!, getOutputFile(), IndyClass(solveClassPath, methodSolveMethod, fieldSolveMethod), withIndyClass, debug, isRuntimeDebug, true, exclusions, excludeTargets))
    }

    val archiveName: String
        get() {
            var name = (jarTask?.baseName ?: "") + this.maybe(jarTask?.baseName, jarTask?.appendix)
            name += this.maybe(name, jarTask?.version)
            name += this.maybe(name, jarTask?.classifier)
            name += this.maybe(name, postfix)
            name += if (jarTask?.extension?.isNotEmpty() == true) "." + jarTask!!.extension else ""
            return name
        }

    private fun maybe(prefix: String?, value: String?): String {
        return if (value?.isNotEmpty() == true) {
            if (prefix?.isNotEmpty() == true) "-$value" else value
        } else {
            ""
        }
    }
}