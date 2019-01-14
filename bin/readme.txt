動作環境
	javaが入っていること。

使い方
	windowsの場合
		run_win.dat [options] [input file/directory] [output file/directory]
	mac/unix/linuxの場合
		run_unix.sh [options] [input file/directory] [output file/directory]

引数
	-classes
		入力と出力をclassesのディレクトリとする。(デフォルト)
	-jar
		入力と出力をjarとする。
	-indyClass [class]
		invokeDynamicで使うクラスのパッケージと名前を指定します。例えば
			com/anatawa12/tools/libs/A
	-indyMethod [name]
		invokeDynamicでmethodのCallSiteを返すメソッドの名前
	-indyField [name]
		invokeDynamicでfieldのCallSiteを返すメソッドの名前
	-withoutIndy
		CallSiteを作るクラスを作りません。
	--debug
		Methodを呼ぶ際にデバッグメッセージをログに流します。
	--
		これのあとはオプションとして認識されません

Copyright (C) anatawa12 2018

この配布物は Apache License 2.0 のライブラリを使用しています。
