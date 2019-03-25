package com.anatawa12.decompileCrasher.core

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.system.exitProcess





/**
 * Created by anatawa12 on 2018/09/08.
 */
object JarRunner {
	fun main(arguments: RunnerArguments) {
		val srcFile = arguments.src
		val dscFile = arguments.dst
		val indyClass = arguments.indyClass
		checkSrcAndDsc(srcFile, dscFile, arguments)


		ZipOutputStream(dscFile.outputStream()).use { outStream ->
			ZipInputStream(srcFile.inputStream()).use { inStream ->
				while (true) {
					val entry = inStream.nextEntry ?: break
					// ディレクトリの場合はスキップ
					if (entry.isDirectory)
						continue
					val outEntry = ZipEntry(entry.name)
					outEntry.time = entry.time
					outEntry.comment = entry.comment
					entry.lastAccessTime?.run { outEntry.lastAccessTime = this }
					entry.lastModifiedTime?.run { outEntry.lastModifiedTime = this }
					entry.creationTime?.run { outEntry.creationTime = this }
					outStream.putNextEntry(outEntry)
					val dotedEntryClassName = entry.name.substringBefore('.').replace('/', '.')
					if (entry.name.substringAfter('.') == "class" && arguments.exclusions.all { !dotedEntryClassName.startsWith(it) }) {
						outStream.write(Obfuscationer.obfuscation(inStream.reads(), indyClass, arguments))
					} else {
						inStream.copyTo(outStream)
					}
				}
			}
			if (arguments.withIndyClass) {
				val entry = ZipEntry(indyClass.classPath + ".class")
				outStream.putNextEntry(entry)
				outStream.write(IndyClassMaker.make(indyClass))
				outStream.closeEntry()
			}
		}
	}

	private fun checkSrcAndDsc(srcFile: File, dscFile: File, arguments: RunnerArguments) {
		if (!srcFile.exists()) errorAndExit("src is not exists")
		if (!srcFile.isFile) errorAndExit("src is not file")

		if (!dscFile.exists()) {
			dscFile.parentFile?.mkdirs()
			return
		}

		if (!dscFile.isFile) errorAndExit("dst is not file")

		if (dscFile.exists() && !arguments.isForce) {
			while (true) {
				System.err.print("Can I clear dst? [Y/n]")
				val line = scanner.nextLine().toLowerCase()
				when (line) {
					"y" -> {
						dscFile.delete()
						return
					}
					"n" -> exitProcess(1)
					else -> System.err.println("Invalid Input.")
				}
			}
		}
	}

	private fun InputStream.reads(): ByteArray {
		val buffer = ByteArrayOutputStream(maxOf(DEFAULT_BUFFER_SIZE, this.available()))
		copyTo(buffer)
		return buffer.toByteArray()
	}
}