/*
 * blanco Framework
 * Copyright (C) 2004-2009 IGA Tosiki
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.cg.valueobject;

/**
 * 言語用のドキュメントのタグを表現するためのバリューオブジェクト。
 *
 * Java言語の場合には JavaDocのタグを表します。
 */
public class BlancoCgLangDocTag {
    /**
     * この言語ドキュメントのタグの名前です。author, seeなどが入ります。
     *
     * フィールド: [name]。
     */
    private String fName;

    /**
     * タグに付けられるキーを指定します。必要の無い場合には無指定とします。
     *
     * フィールド: [key]。
     */
    private String fKey;

    /**
     * このタグの値です。
     *
     * フィールド: [value]。
     */
    private String fValue;

    /**
     * フィールド [name] の値を設定します。
     *
     * フィールドの説明: [この言語ドキュメントのタグの名前です。author, seeなどが入ります。]。
     *
     * @param argName フィールド[name]に設定する値。
     */
    public void setName(final String argName) {
        fName = argName;
    }

    /**
     * フィールド [name] の値を取得します。
     *
     * フィールドの説明: [この言語ドキュメントのタグの名前です。author, seeなどが入ります。]。
     *
     * @return フィールド[name]から取得した値。
     */
    public String getName() {
        return fName;
    }

    /**
     * フィールド [key] の値を設定します。
     *
     * フィールドの説明: [タグに付けられるキーを指定します。必要の無い場合には無指定とします。]。
     *
     * @param argKey フィールド[key]に設定する値。
     */
    public void setKey(final String argKey) {
        fKey = argKey;
    }

    /**
     * フィールド [key] の値を取得します。
     *
     * フィールドの説明: [タグに付けられるキーを指定します。必要の無い場合には無指定とします。]。
     *
     * @return フィールド[key]から取得した値。
     */
    public String getKey() {
        return fKey;
    }

    /**
     * フィールド [value] の値を設定します。
     *
     * フィールドの説明: [このタグの値です。]。
     *
     * @param argValue フィールド[value]に設定する値。
     */
    public void setValue(final String argValue) {
        fValue = argValue;
    }

    /**
     * フィールド [value] の値を取得します。
     *
     * フィールドの説明: [このタグの値です。]。
     *
     * @return フィールド[value]から取得した値。
     */
    public String getValue() {
        return fValue;
    }

    /**
     * このバリューオブジェクトの文字列表現を取得します。
     *
     * <P>使用上の注意</P>
     * <UL>
     * <LI>オブジェクトのシャロー範囲のみ文字列化の処理対象となります。
     * <LI>オブジェクトが循環参照している場合には、このメソッドは使わないでください。
     * </UL>
     *
     * @return バリューオブジェクトの文字列表現。
     */
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("blanco.cg.valueobject.BlancoCgLangDocTag[");
        buf.append("name=" + fName);
        buf.append(",key=" + fKey);
        buf.append(",value=" + fValue);
        buf.append("]");
        return buf.toString();
    }

    /**
     * このバリューオブジェクトを指定のターゲットに複写します。
     *
     * <P>使用上の注意</P>
     * <UL>
     * <LI>オブジェクトのシャロー範囲のみ複写処理対象となります。
     * <LI>オブジェクトが循環参照している場合には、このメソッドは使わないでください。
     * </UL>
     *
     * @param target target value object.
     */
    public void copyTo(final BlancoCgLangDocTag target) {
        if (target == null) {
            throw new IllegalArgumentException("Bug: BlancoCgLangDocTag#copyTo(target): argument 'target' is null");
        }

        // No needs to copy parent class.

        // Name: fName
        // Type: java.lang.String
        target.fName = this.fName;
        // Name: fKey
        // Type: java.lang.String
        target.fKey = this.fKey;
        // Name: fValue
        // Type: java.lang.String
        target.fValue = this.fValue;
    }
}
