/*
 * blanco Framework
 * Copyright (C) 2004-2006 IGA Tosiki
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.cg.transformer;

import blanco.cg.valueobject.BlancoCgSourceFile;
import blanco.commons.util.BlancoFileUtil;
import blanco.commons.util.BlancoStringUtil;

import java.io.*;
import java.util.List;

/**
 * php スタイルの抽象トランスフォーマーです。
 * 
 * @author haino2
 */
public abstract class AbstractBlancoCgPhpStyleTransformer extends
        AbstractBlancoCgTransformer {
    /**
     * デバッグモードで動作させるかどうか。
     */
    private static final boolean IS_DEBUG = false;

    /**
     * ソースファイル・バリューオブジェクトをJavaソースコードに変換して出力先ディレクトリに出力します。
     *
     * このAPIではパッケージ構造をディレクトリ構造として考慮します。
     *
     * @param argSourceFile
     *            ソースファイル・バリューオブジェクト。
     * @param outputDirectory
     *            出力先ルートディレクトリ。
     */
    public void transform(final BlancoCgSourceFile argSourceFile,
            final File outputDirectory) {
        if (argSourceFile == null) {
            throw new IllegalArgumentException("ソースファイルにnullが与えられました。処理中断します。");
        }
        if (outputDirectory == null) {
            throw new IllegalArgumentException(
                    "出力先ルートディレクトリにnullが与えられました。処理中断します。");
        }

        if (outputDirectory.exists() == false) {
            if (outputDirectory.mkdirs() == false) {
                throw new IllegalArgumentException("出力先ルートディレクトリ["
                        + outputDirectory.getAbsolutePath()
                        + "]が存在しなかったので作成しようとしましたがディレクトリ作成に失敗しました。処理中断します。");
            }
        }
        if (outputDirectory.isDirectory() == false) {
            throw new IllegalArgumentException("出力先ルートディレクトリにディレクトリではないファイル["
                    + outputDirectory.getAbsolutePath() + "]が与えられました。処理中断します。");
        }

        if (argSourceFile.getName() == null) {
            // ファイル名が確定していないので、クラス名またはインタフェース名から導出します。
            decideFilenameFromClassOrInterfaceName(argSourceFile);
        }

        try {
            // パッケージ名からディレクトリ名へと変換。
            // 先頭にバックスラッシュがある場合、バックスラッシュを外してつめる
            if (argSourceFile.getPackage() == null) {
                argSourceFile.setPackage("");
            }
            if (argSourceFile.getPackage().startsWith("\\")){
                String nameSpace = argSourceFile.getPackage().substring(1);
                argSourceFile.setPackage(nameSpace);
            }
            String strSubdirectory = BlancoStringUtil.replaceAll(
                    BlancoStringUtil.null2Blank(argSourceFile.getPackage()),
                    '\\', '/');

            if (strSubdirectory.length() > 0) {
                // サブディレクトリが存在する場合にのみスラッシュを追加します。
                strSubdirectory = "/" + strSubdirectory;
            }

            final File targetPackageDirectory = new File(outputDirectory
                    .getAbsolutePath()
                    + strSubdirectory);
            if (targetPackageDirectory.exists() == false) {
                if (targetPackageDirectory.mkdirs() == false) {
                    throw new IllegalArgumentException("出力先のパッケージディレクトリ["
                            + targetPackageDirectory.getAbsolutePath()
                            + "]の生成に失敗しました。");
                }
            }

            // 出力先のファイルを確定します。
            final File fileTarget = new File(targetPackageDirectory
                    .getAbsolutePath()
                    + "/" + argSourceFile.getName() + getSourceFileExt());

            // 実際のソースコード出力処理を行います。
            final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            // 自動生成するソースコードのエンコーディング指定機能
            OutputStreamWriter streamWriter = null;
            if (BlancoStringUtil.null2Blank(argSourceFile.getEncoding())
                    .length() == 0) {
                streamWriter = new OutputStreamWriter(outStream);
            } else {
                streamWriter = new OutputStreamWriter(outStream, argSourceFile
                        .getEncoding());
            }

            final BufferedWriter writer = new BufferedWriter(streamWriter);
            try {
                transform(argSourceFile, writer);
                writer.flush();
                outStream.flush();

                switch (BlancoFileUtil.bytes2FileIfNecessary(outStream
                        .toByteArray(), fileTarget)) {
                case 0:
                    if (IS_DEBUG) {
                        // デバッグ時のみスキップを標準出力。
                        System.out.println(CMDLINE_PREFIX + "skip  : "
                                + fileTarget.getAbsolutePath());
                    }
                    break;
                case 1:
                    System.out.println(CMDLINE_PREFIX + "create: "
                            + fileTarget.getAbsolutePath());
                    break;
                case 2:
                    System.out.println(CMDLINE_PREFIX + "update: "
                            + fileTarget.getAbsolutePath());
                    break;
                }
            } finally {
                // ByteArrayOutputStreamのインスタンスは writerのクローズによって
                // ストリームチェインの仕組み上 自動的にクローズされます。

                if (writer != null) {
                    writer.close();
                }
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("ソースコードを出力する過程で例外が発生しました。"
                    + ex.toString());
        }
    }

    /**
     * ソースコードのリストを整形します。
     *
     * Java言語 および C#.NET言語用の整形を行います。
     *
     * 現時点でのソース整形ルーチンは Java/C#.NET共通と考えることができると判断します。<br>
     * なお、この処理のなかで { や } は特別な意味を持っています。行末コメントなどが入ると期待する動作ができません。<br>
     * TODO 中カッコを文末に付与する、などのフォーマットなどは未実装です。
     *
     * @param argSourceLines
     *            ソースコード行リスト。
     */
    protected void formatSource(final List<String> argSourceLines) {
        int sourceIndent = 0;
        for (int index = 0; index < argSourceLines.size(); index++) {
            String strLine = argSourceLines.get(index);
            // 前後の空白は、あらかじめ除去します。
            strLine = strLine.trim();
            if (strLine.length() == 0) {
                // 空行です。
            } else {
                boolean isBeginIndent = false;
                boolean isEndIndent = false;
                final char startChar = strLine.charAt(0);
                final char endChar = strLine.charAt(strLine.length() - 1);
                if (startChar == '*') {
                    // コメント行と見なして一文字字下げします。
                    strLine = " " + strLine;
                }

                // まずは開始文字列の判定を行います。
                // ※開始文字列と終了文字列とは別個に判定する必要があります。
                if (startChar == '{') {
                    // ブロック開始と見なして字下げを予約します。
                    isBeginIndent = true;
                } else if (startChar == '}') {
                    // ブロック終了と見なして字下げします。
                    isEndIndent = true;
                }

                // 次に終了文字列の判定を行います。
                // ※開始文字列と終了文字列とは別個に判定する必要があります。
                if (endChar == '{') {
                    // ブロック開始と見なして字下げを予約します。
                    isBeginIndent = true;
                } else if (endChar == '}') {
                    // ブロック終了と見なして字下げします。
                    isEndIndent = true;
                }

                if (isEndIndent) {
                    // フラグ一回につき、インデント一個を反映します。
                    sourceIndent--;
                }

                // インデントを実施します。
                for (int indexIndent = 0; indexIndent < sourceIndent; indexIndent++) {
                    // 4タブで字下げします。
                    strLine = "    " + strLine;
                }
                if (isBeginIndent) {
                    sourceIndent++;
                }

                // 更新後の行イメージでリストを更新します。
                argSourceLines.set(index, strLine);
            }
        }
    }
}
