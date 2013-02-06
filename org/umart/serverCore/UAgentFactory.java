package org.umart.serverCore;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.umart.strategyCore.UBaseAgent;


/**
 * UBaseAgentの派生クラスを生成するためのファクトリクラスです．
 * U-Mart Version 1のStrategyの派生クラスも自動的に判別して生成します．
 * @author 小野　功
 *
 */
public class UAgentFactory {
  /**
   * U-Mart Version 1におけるStrategyの派生クラスを生成する．
   * @param userName ログイン名
   * @param passwd パスワード
   * @param className クラス名
   * @param paramsStr エージェントのシステムパラメータを表す文字列
   * @param seed 乱数の種
   * @return UBaseStrategyの派生クラスのオブジェクト
   */
  private static UBaseAgent makeOldTypeStrategy(String loginName,
      String passwd,
      String className, String paramsStr, int seed,
      OutputStream os) {
    UBaseAgent result = null;
    try {
      Class targetClass = Class.forName(className);
      Class[] paramTypes = new Class[1];
      paramTypes[0] = Integer.TYPE;
      Constructor constructor = targetClass.getConstructor(paramTypes);
      Object[] args = new Object[1];
      args[0] = new Integer(seed);
      result = (UBaseAgent) constructor.newInstance(args);
      result.setLoginName(loginName);
      result.setPasswd(passwd);
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
      result.setRealName(className);
    } catch (NoSuchMethodException nme) {
      result = null;
    } finally {
      return result;
    }
  }

  /**
   * UBaseAgentの派生クラスを生成する．
   * @param userName ユーザー名
   * @param passwd パスワード
   * @param className クラス名
   * @param paramsStr エージェントのシステムパラメータを表す文字列
   * @param seed 乱数の種
   * @return UBaseAgentの派生クラスのオブジェクト
   */
  private static UBaseAgent makeNewTypeAgent(String userName, String passwd, String className, String paramsStr, int seed, OutputStream os) {
    UBaseAgent result = null;
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
      result = (UBaseAgent) constructor.newInstance(args);
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
   * UBaseAgentの派生クラスを生成する．
   * このメソッドは旧バージョンにおけるStrategyクラスの派生クラスのオブジェクトも生成可能である．
   * @param userName ユーザー名
   * @param passwd パスワード
   * @param クラス名
   * @param paramsStr エージェントのシステムパラメータを表す文字列
   * @param seed 乱数の種
   * @param os 出力ストリーム
   * @return UBaseAgentの派生クラスのオブジェクト
   */
  public static UBaseAgent makeAgent(String userName, String passwd,
                                       String className, String paramsStr,
                                       int seed, OutputStream os) throws IllegalArgumentException {
    UBaseAgent result = makeNewTypeAgent(userName, passwd, className,
                                         paramsStr, seed, os);
    if (result == null) {
      result = makeOldTypeStrategy(userName, passwd, className, paramsStr, seed, os);
      if (result == null) {
        throw new IllegalArgumentException();
      }
    }
    return result;
  }

  /**
   * UBaseAgentの派生クラスを生成する．
   * @param userName ユーザ名
   * @param passwd パスワード
   * @param className クラス名
   * @param paramsStr エージェントのシステムパラメータを表す文字列
   * @param seed 乱数の種
   * @return UBaseAgentの派生クラスのオブジェクト
   * @throws IllegalArgumentException
   */
  public static UBaseAgent makeAgent(String userName, String passwd,
                                       String className, String paramsStr,
                                       int seed) throws IllegalArgumentException {
    return makeAgent(userName, passwd, className, paramsStr, seed, System.out);
  }

}