package com.anatawa12.decompileCrasher.core

import java.io.File

/**
 * Created by anatawa12 on 2018/09/09.
 */
class RunnerArguments(val src: File,
                      val dst: File,
                      val indyClass: IndyClass,
                      val withIndyClass: Boolean,
                      val debug: Boolean,
                      val isRuntimeDebug: Boolean,
                      val isForce: Boolean,
                      val exclusions: Set<String>,
                      val excludeTargets: List<MethodFullSignature>)

data class MethodFullSignature(
        val className: String,
        val methodName: String,
        /**
         * if null, check only methodName
         */
        val methodSignature: String?
) {
    override fun toString(): String {
        if (methodSignature != null)
            return "$className.$methodName:$methodSignature"
        else
            return "$className.$methodName"
    }

    companion object {
        fun perse(text: String): MethodFullSignature {
            var i = 0
            val className = buildString {
                w@ while (i < text.length) {
                    when (val c = text[i]) {
                        '.' -> break@w // next is method name
                        ';', '[', '<', '>', ':' -> throw IllegalArgumentException("invalid signature: $text, invalid char $c at $i")
                        else -> append(c)
                    }
                    i++
                }
            }
            i++// skip '.'
            require(className.isNotEmpty()) { "invalid signature: $text, class name is empty" }
            val methodName = buildString {
                w@ while (i < text.length) {
                    when (val c = text[i]) {
                        ':' -> break@w // next is method signature
                        '.', ';', '[', '<', '>', ':' -> throw IllegalArgumentException("invalid signature: $text, invalid char $c at $i")
                        else -> append(c)
                    }
                    i++
                }
            }
            require(methodName.isNotEmpty()) { "invalid signature: $text, method name is empty" }
            if (i != text.length) {
                i++// skip ':'
                val signature = buildString {
                    require(text[i] == '(') { "invalid signature: $text, invalid char ${text[i]} at $i" }
                    append(text[i])
                    i++//skip '('

                    w@ while (i < text.length) {
                        ary@ while (i < text.length) {
                            when (val c = text[i]) {
                                '[' -> append(c)
                                else -> break@ary
                            }
                            i++// skip '['
                        }
                        when (val c = text[i]) {
                            'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z' -> append(c)
                            'L' -> {
                                append(c)
                                i++//skip 'L'
                                clsName@ while (i < text.length) {
                                    when (val c = text[i]) {
                                        ';' -> break@clsName // next is method name
                                        '.', '[', '<', '>', ':' -> throw IllegalArgumentException("invalid signature: $text, invalid char $c at $i")
                                        else -> append(c)
                                    }
                                    i++
                                }
                                append(text[i])
                            }
                            ')' -> break@w
                            else -> throw IllegalArgumentException("invalid signature: $text, invalid char $c at $i")
                        }
                        i++// skip 'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z', ';'
                    }
                    require(text[i] == ')') { "invalid signature: $text, unexpexted end of string" }
                    append(text[i])
                    i++ // skip ')'
                    ary@ while (i < text.length) {
                        when (val c = text[i]) {
                            '[' -> append(c)
                            else -> break@ary
                        }
                        i++// skip '['
                    }
                    when (val c = text[i]) {
                        'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z', 'V' -> append(c)
                        'L' -> {
                            append(c)
                            i++//skip 'L'
                            clsName@ while (i < text.length) {
                                when (val c = text[i]) {
                                    ';' -> break@clsName // next is method name
                                    '.', '[', '<', '>', ':' -> throw IllegalArgumentException("invalid signature: $text, invalid char $c at $i")
                                    else -> append(c)
                                }
                                i++
                            }
                            append(text[i])
                        }
                        else -> throw IllegalArgumentException("invalid signature: $text, invalid char $c at $i")
                    }
                    i++// skip 'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z', ';'
                    require(i == text.length) { "invalid signature: $text, unexpexted end of string" }
                }
                return MethodFullSignature(className, methodName, signature)
            } else {
                return MethodFullSignature(className, methodName, null)
            }
        }
    }
}
