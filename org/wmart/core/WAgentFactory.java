package org.wmart.core;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.wmart.agent.*;

/**
 * WBaseAgentの派生クラスを生成するためのファクトリクラスです．
 * 
 * @author 小野　功
 * @author Ikki Fujiwara, NII
 * 
 */
@SuppressWarnings("finally")
public class WAgentFactory {
	/**
	 * WBaseAgentの派生クラスを生成する．
	 * 
	 * @param userName
	 *            ユーザー名
	 * @param passwd
	 *            パスワード
	 * @param className
	 *            クラス名
	 * @param paramsStr
	 *            エージェントのシステムパラメータを表す文字列
	 * @param seed
	 *            乱数の種
	 * @return WBaseAgentの派生クラスのオブジェクト
	 */
	private static WBaseAgent makeNewTypeAgent(String userName, String passwd, String className, String paramsStr,
		int seed, OutputStream os) {
		WBaseAgent result = null;
		try {
			Class targetClass = Class.forName(className);
			Class[] paramTypes = new Class[4];
			paramTypes[0] = userName.getClass();
			paramTypes[1] = passwd.getClass();
			paramTypes[2] = className.getClass();
			paramTypes[3] = Integer.TYPE;
			Constructor constructor = targetClass.getConstructor(paramTypes);
			Object[] args = new Object[4];
			args[0] = userName;
			args[1] = passwd;
			args[2] = className;
			args[3] = new Integer(seed);
			result = (WBaseAgent) constructor.newInstance(args);
			result.setOutputStream(os);
			String[] params = new String[0];
			if (paramsStr != null) {
				StringTokenizer st = new StringTokenizer(paramsStr, ":");
				params = new String[st.countTokens()];
				for (int i = 0; i < params.length; ++i) {
					params[i] = st.nextToken();
				}
			}
			result.setParameters(params);
		} catch (NoSuchMethodException nme) {
			result = null;
		} finally {
			return result;
		}
	}

	/**
	 * WBaseAgentの派生クラスを生成する．
	 * 
	 * @param userName
	 *            ユーザー名
	 * @param passwd
	 *            パスワード
	 * @param クラス名
	 * @param paramsStr
	 *            エージェントのシステムパラメータを表す文字列
	 * @param seed
	 *            乱数の種
	 * @param os
	 *            出力ストリーム
	 * @return WBaseAgentの派生クラスのオブジェクト
	 */
	public static WBaseAgent makeAgent(String userName, String passwd, String className, String paramsStr, int seed,
		OutputStream os) throws IllegalArgumentException {
		WBaseAgent result = makeNewTypeAgent(userName, passwd, className, paramsStr, seed, os);
		if (result == null) {
			throw new IllegalArgumentException();
		}
		return result;
	}

	/**
	 * WBaseAgentの派生クラスを生成する．
	 * 
	 * @param userName
	 *            ユーザ名
	 * @param passwd
	 *            パスワード
	 * @param className
	 *            クラス名
	 * @param paramsStr
	 *            エージェントのシステムパラメータを表す文字列
	 * @param seed
	 *            乱数の種
	 * @return WBaseAgentの派生クラスのオブジェクト
	 * @throws IllegalArgumentException
	 */
	public static WBaseAgent makeAgent(String userName, String passwd, String className, String paramsStr, int seed)
		throws IllegalArgumentException {
		return makeAgent(userName, passwd, className, paramsStr, seed, System.out);
	}

}