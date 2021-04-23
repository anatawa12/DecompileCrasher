package com.anatawa12.tools.decompileCrasher.gradle

import com.anatawa12.decompileCrasher.core.IndyClass
import com.anatawa12.decompileCrasher.core.JarRunner
import com.anatawa12.decompileCrasher.core.MethodFullSignature
import com.anatawa12.decompileCrasher.core.RunnerArguments
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.jvm.tasks.Jar
import java.io.File

/**
 * Created by anatawa12 on 2018/11/14.
 */
@CacheableTask
open class ObfuscationTask() : DefaultTask() {
    @Internal
    var jarTask: Jar? = null

    init {
        group = "build"
        description = "runDecompileCrasher with DecompileCrasher"
    }

    @get:Input
    var withIndyClass = true

    @get:Input
    var debug = false

    @get:Input
    var isRuntimeDebug = false

    @get:Input
    var solveClassPath: String = IndyClass.default.classPath

    @get:Input
    var methodSolveMethod: String = IndyClass.default.method

    @get:Input
    var fieldSolveMethod: String = IndyClass.default.field

    // a part of getOutputFile
    @get:Internal
    var destinationDir: File? = null

    @get:Input
    var postfix: String? = "obfuscated"

    @get:Input
    val exclusions: MutableSet<String> = mutableSetOf()

    fun exclusions(vararg exclusions: String) {
        this.exclusions.addAll(exclusions)
    }

    @Deprecated("binary compatible", level = DeprecationLevel.HIDDEN)
    @Suppress("UNUSED_PARAMETER")
    @JvmOverloads
    fun getInputFile(dummy: Int = 0): File? = jarTask?.archiveFile?.get()?.asFile

    @InputFile
    @PathSensitive(PathSensitivity.NONE)
    fun getInputFile(): Provider<RegularFile> = jarTask!!.archiveFile

    @OutputFile
    fun getOutputFile(): File = File(destinationDir, archiveName)

    @Input
    val excludeTargets: MutableList<MethodFullSignature> = mutableListOf()

    fun excludeTarget(name: String) {
        excludeTargets += MethodFullSignature.perse(name)
    }

    @TaskAction
    fun runDecompileCrasher() {
        if (jarTask == null) error("input file is null")
        JarRunner.main(RunnerArguments(getInputFile().get().asFile, getOutputFile(), IndyClass(solveClassPath, methodSolveMethod, fieldSolveMethod), withIndyClass, debug, isRuntimeDebug, true, exclusions, excludeTargets))
    }

    // a part of getOutputFile
    @get:Internal
    val archiveName: String
        get() {
            var name = (jarTask?.archiveBaseName?.get() ?: "") + this.maybe(jarTask?.archiveBaseName?.get(), jarTask?.archiveAppendix?.get())
            name += this.maybe(name, jarTask?.archiveVersion?.get())
            name += this.maybe(name, jarTask?.archiveClassifier?.get())
            name += this.maybe(name, postfix)
            name += if (jarTask?.archiveExtension?.get()?.isNotEmpty() == true) "." + jarTask!!.archiveExtension.get() else ""
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
