Operating environment
	java is installed

How to use
	on windows
		run_win.bat [options] [input file/directory] [output file/directory]
	on mac/unix/linux
		run_unix.sh [options] [input file/directory] [output file/directory]

arguments
	-classes
		Make inputs and outputs classes directories. (Default)
	-jar
		Make input and output jar
	-indyClass [class]
		Specify the package and name of the class to use with invokeDynamic. For example,
			com/anatawa12/tools/libs/A
	-indyMethod [name]
		The name of the method that returns the method's CallSite with invokeDynamic. (default: "m")
	-indyField [name]
		The name of the method that returns the field's CallSite with invokeDynamic. (default: "m")
	-withoutIndy
		Don't make Class to make CallSite.
	--debug
		the when Invoke Method, make debug messages/
	--
		This is not recognized as an option after this

Copyright (C) anatawa12 2018

This distribution uses the Apache License 2.0 library.
