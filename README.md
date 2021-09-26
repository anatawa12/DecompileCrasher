# Decompile Crasher

[![a12 maintenance: Slowly](https://api.anatawa12.com/short/a12-slowly-svg)](https://api.anatawa12.com/short/a12-slowly-doc)
[![Discord](https://img.shields.io/discord/834256470580396043)](https://discord.gg/yzEdnuJMXv)
<!--[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com/anatawa12/tools/decompileCrasher/com.anatawa12.tools.decompileCrasher.plugin/maven-metadata.xml.svg?colorB=007ec6&label=gradle&logo=gradle)](https://plugins.gradle.org/plugin/com.anatawa12.compile-time-constant)-->

A tool to make a Jar Hard to decompile.
Decompiler will make invalid source code or internal error will be thrown

## Sample
### decompilable code

![Main.java](readme-contents/source_vsc.png?raw=true)

### decompiled output of this tool
#### JD-GUI

![jd gui](readme-contents/jd_gui_decompile.png?raw=true)

#### intellij

![idea](readme-contents/idea_decompile.png?raw=true)

## how-to-use
### with gradle 
add this
```
buildscript {
    dependces {
        classpath group: 'com.anatawa12.decompileCrasher', name: 'gradle-plugin', version: '<version>'
    }
}

apply plugin: 'com.anatawa12.tools.decompileCrasher'
```
task class signature is like this(in real, we use getter and setter from Kotlin)
```
class ObfuscationTask {
    // jar task to make jar which will obfuscation with this
    // get some data from jar task.
    // if you want to make ObfuscationTask, please set this field.
    Jar jarTask;

    // a class path to make CallSite
    // default value is "com/anatawa12/tools/lib/A"
    String solveClassPath;
    // method name of make CallSite for method
    // default value is "m"
    String methodSolveMethod;
    // method name of make CallSite for field(now not allowed)
    // default value is "m"
    String fieldSolveMethod;

    // output directory
    // defaut value is "$buildDir/libs"
    File destinationDir;
    // postfix of jar name. this will put after classfier
    // defaut value is "obfuscated"
    String postfix;

    // prefixes of class name which do not anything in this tool
    // default value is empty set
    void exclusions(String... exclusions);
    final Set<String> exclusions;

    // output class to make CallSite in the jar. if false, you must make it yourself.
    // default value is true
    boolean withIndyClass;
}
```

### with Command line tools

download from Releases and see `readme.en.txt` or exec `run_unix.sh` or `run_win.dat` (please select by your OS).

## License

this software was released under [MIT License](LICENSE)
