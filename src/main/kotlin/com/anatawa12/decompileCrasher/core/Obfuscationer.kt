package com.anatawa12.decompileCrasher.core

import org.objectweb.asm.*
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassWriter.COMPUTE_MAXS
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type.*
import java.io.PrintStream
import java.lang.invoke.CallSite
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

/**
 * Created by anatawa12 on 2018/09/08.
 */
class Obfuscationer(api: Int, classVisitor: ClassVisitor, val indyClass: IndyClass, val runnerArguments: RunnerArguments) : ClassVisitor(api, classVisitor) {
    lateinit var cName: String
    lateinit var dottedCName: String
    override fun visit(version: Int, access: Int, name: String, signature: String?, superName: String?, interfaces: Array<out String>?) {
        cName = name
        dottedCName = cName.replace('/', '.')
        super.visit(maxOf(version, V1_7), access, name, signature, superName, interfaces)
    }

    override fun visitMethod(access: Int, mName: String, descriptor: String, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val fullMethodName = "$dottedCName.$mName"
        val fullMethodDesc = "$dottedCName.$mName$descriptor"
        if (mName == "<init>" || mName == "<clinit>"
                || fullMethodName in runnerArguments.exclusions
                || fullMethodDesc in runnerArguments.exclusions) {
            return super.visitMethod(access, mName, descriptor, signature, exceptions)
        } else {
            return object : MethodVisitor(api, super.visitMethod(access, mName, descriptor, signature, exceptions)) {
                override fun visitMethodInsn(opcode: Int, owner: String, name: String, descriptor: String, isInterface: Boolean) {
                    if (MethodFullSignature(owner, name, descriptor) in runnerArguments.excludeTargets)
                        return super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)

                    val ownerType = getObjectType(owner)
                    val returnType = getReturnType(descriptor)
                    val arguments = getArgumentTypes(descriptor)
                    val descType = getMethodType(descriptor)

                    if (owner[0] == '[') return super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)

                    val desc: String
                    val callType: Int

                    when (opcode) {
                        INVOKEVIRTUAL -> {
                            desc = getMethodDescriptor(returnType, ownerType, *arguments)
                            callType = 0
                        }
                        INVOKESPECIAL -> {
                            return super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                        }
                        INVOKESTATIC -> {
                            desc = descriptor
                            callType = 1
                        }
                        INVOKEINTERFACE -> {
                            desc = getMethodDescriptor(returnType, ownerType, *arguments)
                            callType = 0
                        }
                        else -> error("")
                    }

                    if (runnerArguments.debug) {
                        val String = getType(String::class.java)
                        val System = getType(System::class.java)
                        val PrintStream = getType(PrintStream::class.java)
                        super.visitFieldInsn(GETSTATIC, System.internalName, "out", "$PrintStream")
                        super.visitLdcInsn("method callType: $owner, $name, $descriptor\nfrom: $cName, $mName")
                        super.visitMethodInsn(INVOKEVIRTUAL, PrintStream.internalName, "println", "($String)V", false)
                    }

                    super.visitInvokeDynamicInsn(
                            "no mName method",
                            desc,
                            Handle(H_INVOKESTATIC,
                                    indyClass.classPath,
                                    indyClass.method,
                                    "(${getDescriptor(MethodHandles.Lookup::class.java)}${getDescriptor(String::class.java)}${getDescriptor(MethodType::class.java)}$INT_TYPE${getDescriptor(Class::class.java)}${getDescriptor(MethodType::class.java)}${getDescriptor(String::class.java)})${getDescriptor(CallSite::class.java)}"),
                            callType,
                            ownerType,
                            descType,
                            name
                    )
                }

                /*
                override fun visitFieldInsn(opcode: Int, owner: String, name: String, descriptor: String) {
                    val ownerType = getObjectType(owner)

                    val callType: Int
                    val desc: String

                    when (opcode) {
                        GETSTATIC -> {
                            callType = 0
                            desc = "()$descriptor"
                        }
                        PUTSTATIC -> {
                            callType = 1
                            desc = "($descriptor)V"
                        }
                        GETFIELD -> {
                            callType = 2
                            desc = "($ownerType)$descriptor"
                        }
                        PUTFIELD -> {
                            callType = 3
                            desc = "($ownerType$descriptor)V"
                        }
                        else -> error("")
                    }

                    if (runnerArguments.debug){
                        val String = getType(String::class.java)
                        val System = getType(System::class.java)
                        val PrintStream = getType(PrintStream::class.java)
                        super.visitFieldInsn(GETSTATIC, System.internalName, "out", "$PrintStream")
                        super.visitLdcInsn("field callType: $owner, $name, $descriptor\nfrom: $cName, $mName")
                        super.visitMethodInsn(INVOKEVIRTUAL, PrintStream.internalName, "println", "($String)V", false)
                    }

                    super.visitInvokeDynamicInsn(
                            "no mName method",
                            desc,
                            Handle(H_INVOKESTATIC,
                                    indyClass.classPath,
                                    indyClass.method,
                                    "(${getDescriptor(MethodHandles.Lookup::class.java)}${getDescriptor(String::class.java)}${getDescriptor(MethodType::class.java)}$INT_TYPE${getDescriptor(Class::class.java)}${getDescriptor(Class::class.java)}${getDescriptor(String::class.java)})${getDescriptor(CallSite::class.java)}"),
                            callType,
                            ownerType,
                            Type.getType(descriptor),
                            name
                    )
                    //super.visitFieldInsn(opcode, owner, mName, descriptor)
                }
                // */
            }
        }
    }

    companion object {
        fun obfuscation(src: ByteArray, indyClass: IndyClass, arguments: RunnerArguments): ByteArray {
            val reader = ClassReader(src)
            val writer = ClassWriter(//COMPUTE_FRAMES or
                    COMPUTE_MAXS)
            val obfuscationer = Obfuscationer(ASM5, writer, indyClass, arguments)
            reader.accept(obfuscationer, /*SKIP_DEBUG or */EXPAND_FRAMES)
            return writer.toByteArray()
        }
    }
}