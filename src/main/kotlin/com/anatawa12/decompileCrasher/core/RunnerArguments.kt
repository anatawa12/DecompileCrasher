package com.anatawa12.decompileCrasher.core

import java.io.File

/**
 * Created by anatawa12 on 2018/09/09.
 */
class RunnerArguments (val src: File,
                       val dst: File,
                       val indyClass: IndyClass,
                       val withIndyClass: Boolean,
                       val debug: Boolean,
                       val isForce: Boolean,
                       val exclusions: List<String>)