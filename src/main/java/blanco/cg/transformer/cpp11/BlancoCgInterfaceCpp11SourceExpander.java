/*
 * blanco Framework
 * Copyright (C) 2004-2017 IGA Tosiki
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
/*
 * Copyright 2017 Toshiki Iga
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package blanco.cg.transformer.cpp11;

import java.util.List;

import blanco.cg.valueobject.BlancoCgField;
import blanco.cg.valueobject.BlancoCgInterface;
import blanco.cg.valueobject.BlancoCgLangDoc;
import blanco.cg.valueobject.BlancoCgMethod;
import blanco.cg.valueobject.BlancoCgSourceFile;
import blanco.cg.valueobject.BlancoCgType;
import blanco.commons.util.BlancoStringUtil;

/**
 * BlancoCgInterfaceをソースコードに展開します。
 * 
 * このクラスはblancoCgのバリューオブジェクトからソースコードを自動生成するトランスフォーマーの個別の展開機能です。
 * 
 * @author IGA Tosiki
 */
class BlancoCgInterfaceCpp11SourceExpander {

    /**
     * ここでinterfaceを展開します。
     * 
     * C#.NETには Java言語と同様なインタフェースが存在します。
     * 
     * @param cgInterface
     *            処理対象となるインタフェース。
     * @param argSourceLines
     *            ソースコード。
     */
    public void transformInterface(final BlancoCgInterface cgInterface,
            final BlancoCgSourceFile argSourceFile,
            final List<java.lang.String> argSourceLines) {
        // インタフェースの場合には Java言語同様にフィールドやメソッドからpublicが除外されます。

        // 最初にインタフェース情報をLangDocに展開。
        if (cgInterface.getLangDoc() == null) {
            // LangDoc未指定の場合にはこちら側でインスタンスを生成。
            cgInterface.setLangDoc(new BlancoCgLangDoc());
        }
        if (cgInterface.getLangDoc().getTitle() == null) {
            cgInterface.getLangDoc().setTitle(cgInterface.getDescription());
        }

        // 次に LangDocをソースコード形式に展開。
        new BlancoCgLangDocCpp11SourceExpander().transformLangDoc(cgInterface
                .getLangDoc(), argSourceLines);

        final StringBuffer buf = new StringBuffer();

        if (BlancoStringUtil.null2Blank(cgInterface.getAccess()).length() > 0) {
            buf.append(cgInterface.getAccess() + ": ");
        }
        // staticやfinalは展開しません。
        buf.append("interface " + cgInterface.getName());

        // ここで親クラスを展開。
        expandExtendClassList(cgInterface, buf);

        // ※ポイント: 親インタフェース展開は interfaceには存在しません。

        argSourceLines.add(buf.toString());

        argSourceLines.add("{");

        // ここでフィールドを展開。
        expandFieldList(cgInterface, argSourceFile, argSourceLines);

        // ここでメソッドを展開。
        expandMethodList(cgInterface, argSourceFile, argSourceLines);

        argSourceLines.add("};");
    }

    /**
     * 親クラスを展開します。
     * 
     * @param cgClass
     * @param buf
     */
    private void expandExtendClassList(final BlancoCgInterface cgClass,
            final StringBuffer buf) {
        for (int index = 0; index < cgClass.getExtendClassList().size(); index++) {
            final BlancoCgType type = cgClass.getExtendClassList().get(index);

            if (index == 0) {
                buf.append(" : "
                        + BlancoCgTypeCpp11SourceExpander.toTypeString(type));
            } else {
                // TODO C#.NETで継承が一度きりという制限があるかどうか確認すること。
                buf.append(", "
                        + BlancoCgTypeCpp11SourceExpander.toTypeString(type));
            }
        }
    }

    /**
     * 含まれる各々のフィールドを展開します。
     * 
     * @param cgInterface
     * @param argSourceFile
     * @param argSourceLines
     */
    private void expandFieldList(final BlancoCgInterface cgInterface,
            final BlancoCgSourceFile argSourceFile,
            final List<java.lang.String> argSourceLines) {
        if (cgInterface.getFieldList() == null) {
            // フィールドのリストにnullが与えられました。
            // かならずフィールドのリストにはListをセットしてください。
            throw new IllegalArgumentException("フィールドのリストにnullが与えられました。");
        }

        for (int index = 0; index < cgInterface.getFieldList().size(); index++) {
            final BlancoCgField cgField = cgInterface.getFieldList().get(index);
            new BlancoCgFieldCpp11SourceExpander().transformField(cgField,
                    argSourceFile, argSourceLines, true);
        }
    }

    /**
     * 含まれる各々のメソッドを展開します。
     * 
     * @param cgInterface
     * @param argSourceFile
     * @param argSourceLines
     */
    private void expandMethodList(final BlancoCgInterface cgInterface,
            final BlancoCgSourceFile argSourceFile,
            final List<java.lang.String> argSourceLines) {
        if (cgInterface.getMethodList() == null) {
            throw new IllegalArgumentException("メソッドのリストにnullが与えられました。");
        }
        for (int index = 0; index < cgInterface.getMethodList().size(); index++) {
            final BlancoCgMethod cgMethod = cgInterface.getMethodList().get(
                    index);
            new BlancoCgMethodCpp11SourceExpander().transformMethod(cgMethod,
                    argSourceFile, argSourceLines, true);
        }
    }
}
