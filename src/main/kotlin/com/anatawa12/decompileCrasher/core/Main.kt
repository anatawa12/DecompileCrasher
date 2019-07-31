@file:JvmName("MainKt")


package com.anatawa12.decompileCrasher.core

import java.io.File
import java.util.*
import kotlin.system.exitProcess

/**
 * Created by anatawa12 on 2018/09/08.
 */
val scanner = Scanner(System.`in`)

fun main(args: Array<String>) {
	var runnerType: RunnerType? = null
	var index = 0
	var indyClass: String? = null
	var indyMethod: String? = null
	var indyField: String? = null
	var options = true
	var src: String? = null
	var dst: String? = null
	var withIndyClass: Boolean? = null
	var debug: Boolean? = null
	var isRuntimeDebug: Boolean? = null
	var isForce = false
	val exclusions = mutableSetOf<String>()
	val excludeTargets = mutableListOf<MethodFullSignature>()
	while (index in args.indices) {
		val arg = args[index]
		if (options) {
			if (arg[0] == '-') {
				when (arg) {
					"-f", "-force" -> {
						isForce = true
					}
					"-jar" -> {
						if (runnerType != null) errorAndExit("can't use two or more -jar or -classes")
						runnerType = RunnerType.Jar
					}
					"-classes" -> {
						if (runnerType != null) errorAndExit("can't use two or more -jar or -classes")
						runnerType = RunnerType.Classes
					}
					"-indyClass" -> {
						if (indyClass != null) errorAndExit("can't duplicate -indyClass option")
						index++
						indyClass = args[index]
					}
					"-indyMethod" -> {
						if (indyMethod != null) errorAndExit("can't duplicate -indyMethod option")
						index++
						indyMethod = args[index]
					}
					"-indyField" -> {
						if (indyField != null) errorAndExit("can't duplicate -indyField option")
						index++
						indyField = args[index]
					}
					"-withoutIndy" -> {
						if (withIndyClass != null) errorAndExit("can't duplicate -withoutIndy option")
						withIndyClass = false
					}
					"-e", "-exclusion" -> {
						index++
						exclusions.add(args[index].replace('/', '.'))
					}
					"--debug" -> {
						if (debug != null) errorAndExit("can't duplicate --debug option")
						debug = true
					}
					"--runtimeDebug" -> {
						if (isRuntimeDebug != null) errorAndExit("can't duplicate --runtimeDebug option")
						isRuntimeDebug = true
					}
					"--help" -> {
						printHelp()
						exitProcess(0)
					}
					"-exclude-target" -> {
						printHelp()
						index++
						excludeTargets += MethodFullSignature.perse(args[index])
					}
					"--" -> {
						options = false
					}
					else -> {
						System.err.println("invalid argument: $arg")
						exitProcess(0)
					}
				}
			} else {
				options = false
				src = arg
			}
		} else {
			if (src == null) {
				src = arg
			} else {
				dst = arg
				break
			}
		}
		index++
	}

	indyClass = indyClass ?: IndyClass.default.classPath
	indyMethod = indyMethod ?: IndyClass.default.method
	indyField = indyField ?: IndyClass.default.field

	if (src == null) errorAndExit("there is no src and dst")
	if (dst == null) dst = makeDstFromSrc(src)

	val srcFile = File(src)
	val dstFile = File(dst)
	if (runnerType == null) {
		runnerType = if (srcFile.isFile) RunnerType.Jar else RunnerType.Classes
	}
	val arguments = RunnerArguments(srcFile, dstFile, IndyClass(indyClass, indyMethod, indyField), withIndyClass
			?: true, debug ?: false, isRuntimeDebug ?: false, isForce, exclusions, excludeTargets)
	when (runnerType) {
		RunnerType.Jar -> JarRunner.main(arguments)
		RunnerType.Classes -> ClassesRunner.main(arguments)
	}
}

fun makeDstFromSrc(src: String): String {
	return src.substringBeforeLast('.') + "-obfuscated" + src.substringAfterLast('.', "")
}

fun printHelp() {
	System.err.println("Usage: kotlin [options] <source> [destination]")
	System.err.println("where possible options include:")
	System.err.println("  -jar                       input and output is jar file")
	System.err.println("  -classes                   input and output is classes directory")
	System.err.println("  -indyClass <class name>    maker class of MethodHandle for invoke dynamic")
	System.err.println("  -indyMethod <method name>  maker method of MethodHandle for invoke dynamic to solve method")
	System.err.println("  -indyField <method name>   maker method of MethodHandle for invoke dynamic to solve field")
	System.err.println("  -withoutIndy               don't make maker class of MethodHandle")
	System.err.println("  -debug                     draw debug message when solve method or field")
	System.err.println("  -help                      view this message")
}

fun errorAndExit(message: String): Nothing {
	System.err.println(message)
	exitProcess(1)
}

enum class RunnerType {
	Jar, Classes
}
