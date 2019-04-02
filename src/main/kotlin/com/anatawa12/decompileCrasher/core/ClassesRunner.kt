package com.anatawa12.decompileCrasher.core

import java.io.File
import java.util.*
import kotlin.system.exitProcess



/**
 * Created by anatawa12 on 2018/09/08.
 */
object ClassesRunner {
	fun main(arguments: RunnerArguments) {
		val srcDir = arguments.src
		val dscDir = arguments.dst
		val indyClass = arguments.indyClass
		checkSrcAndDsc(srcDir, dscDir, arguments)
		val srcFiles = findAllFile(srcDir)

		for (srcFile in srcFiles) {
			val relative = srcDir.toURI().relativize(srcFile.toURI()).toString()
			val dscFile = dscDir.resolve(relative)
			dscFile.parentFile.mkdirs()
			val dotedEntryClassName = srcFile.nameWithoutExtension.replace('/', '.')
			if (srcFile.extension == "class" && dotedEntryClassName !in arguments.exclusions && arguments.exclusions.all { !dotedEntryClassName.startsWith("$it.") }) {
				dscFile.writeBytes(Obfuscationer.obfuscation(srcFile.readBytes(), indyClass, arguments))
			} else {
				srcFile.copyTo(dscFile)
			}
		}
		if (arguments.withIndyClass) {
			val file = dscDir.resolve(indyClass.classPath + ".class")
			file.parentFile.mkdirs()
			file.outputStream().use {
				it.write(IndyClassMaker.make(indyClass, arguments.isRuntimeDebug))
			}
		}
	}

	private fun checkSrcAndDsc(srcDir: File, dscDir: File, arguments: RunnerArguments) {
		if (!srcDir.exists()) errorAndExit("src is not exists")
		if (!srcDir.isDirectory) errorAndExit("src is not directory")

		if (!dscDir.exists()) {
			dscDir.mkdirs()
		}

		if (!dscDir.isDirectory) errorAndExit("dst is not directory")

		if (dscDir.list()?.isNotEmpty() == true && !arguments.isForce) {
			while (true) {
				System.err.print("Can I clear dst? [Y/n]")
				val line = scanner.nextLine().toLowerCase()
				when (line) {
					"y" -> {
						deleteFolder(dscDir)
						dscDir.mkdirs()
						return
					}
					"n" -> exitProcess(1)
					else -> System.err.println("Invalid Input.")
				}
			}
		}
	}

	fun deleteFolder(folder: File) {
		val files = folder.listFiles()
		if (files != null) { //some JVMs return null for empty dirs
			for (f in files) {
				if (f.isDirectory) {
					deleteFolder(f)
				} else {
					f.delete()
				}
			}
		}
		folder.delete()
	}

	fun findAllFile(absolutePath: File): List<File> {
		val files = ArrayList<File>()

		val stack = Stack<File>()
		stack.add(absolutePath)
		while (!stack.isEmpty()) {
			val item = stack.pop()
			if (item.isFile) files.add(item)

			if (item.isDirectory) {
				for (child in item.listFiles()!!) stack.push(child)
			}
		}

		return files
	}
}