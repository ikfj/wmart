/*
 * Created on 2003/08/01
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.umart.util;

import java.util.*;

/**
 * @author isao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Utility {

  public static String makeKeyString(String type, String name) {
    StringTokenizer st = new StringTokenizer(name, "_");
    String keyStr = type.toUpperCase();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      keyStr += "_" + token.toUpperCase();
    }
    return keyStr;
  }

  public static String makeFieldString(String name) {
    StringTokenizer st = new StringTokenizer(name, "_");
    String variableName = "f";
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      variableName += capitalize(token);
    }
    return variableName;
  }

  public static String capitalize(String str) {
    String begin = str.substring(0, 1);
    String rest = str.substring(1);
    return begin.toUpperCase() + rest;
  }

  public static String makeLocalVariableString(String name) {
    StringTokenizer st = new StringTokenizer(name, "_");
    String variableName = st.nextToken();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      variableName += capitalize(token);
    }
    return variableName;
  }

  public static String makeLocalVariable(String name) {
    StringTokenizer st = new StringTokenizer(name, "_");
    String result = st.nextToken();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      result += Utility.capitalize(token);
    }
    return result;
  }

  public static String makeStringForGettingDataFromHash(String hashName,
      HashMap dataInfo, String cmdName) {
    String type = (String) dataInfo.get("TYPE");
    String name = (String) dataInfo.get("NAME");
    String comment = (String) dataInfo.get("COMMENT");
    String keyStr = "UC" + cmdName + "Core." + Utility.makeKeyString(type, name);
    if (type.equals("boolean")) {
      return "((Boolean)" + hashName + ".get(" + keyStr + ")).booleanValue();";
    } else if (type.equals("int")) {
      return "((Integer)" + hashName + ".get(" + keyStr + ")).intValue();";
    } else if (type.equals("long")) {
      return "((Long)" + hashName + ".get(" + keyStr + ")).longValue();";
    } else if (type.equals("double")) {
      return "((Double)" + hashName + ".get(" + keyStr + ")).doubleValue();";
    } else if (type.equals("String")) {
      return "(String)" + hashName + ".get(" + keyStr + ")";
    } else if (type.equals("HashName")) {
      return "(HashName)" + hashName + ".get(" + keyStr + ")";
    } else if (type.equals("ArrayList")) {
      return "(ArrayList)" + hashName + ".get(" + keyStr + ")";
    } else {
      System.err.println("Unknown type: " + type +
                         " in Utility.makeStringForGettingDataFromHash");
      System.exit(5);
      return null;
    }
  }

  public static String makeStringForGettingDataFromIterator(String itrName,
      HashMap dataInfo) {
    String type = (String) dataInfo.get("TYPE");
    String name = (String) dataInfo.get("NAME");
    String comment = (String) dataInfo.get("COMMENT");
    if (type.equals("boolean")) {
      return "((Boolean)" + itrName + ".next()).booleanValue();";
    } else if (type.equals("int")) {
      return "((Integer)" + itrName + ".next()).intValue();";
    } else if (type.equals("long")) {
      return "((Long)" + itrName + ".next()).longValue();";
    } else if (type.equals("double")) {
      return "((Double)" + itrName + ".next()).doubleValue();";
    } else if (type.equals("String")) {
      return "(String)" + itrName + ".next()";
    } else if (type.equals("HashName")) {
      return "(HashName)" + itrName + ".next()";
    } else if (type.equals("ArrayList")) {
      return "(ArrayList)" + itrName + ".next()";
    } else {
      System.err.println("Unknown type: " + type +
                         " in Utility.makeStringForGettingDataFromIterator");
      System.exit(5);
      return null;
    }
  }

  public static String makeStringForPuttingDataToHash(String hashName,
      String coreClassName,
      HashMap dataInfo) {
    String type = (String) dataInfo.get("TYPE");
    String name = Utility.makeLocalVariable( (String) dataInfo.get("NAME"));
    String comment = (String) dataInfo.get("COMMENT");
    String key = Utility.makeKeyString(type, (String) dataInfo.get("NAME"));
    if (type.equals("boolean")) {
      return hashName + ".put(" + coreClassName + "." + key + ", new Boolean(" +
          name + "));";
    } else if (type.equals("int")) {
      return hashName + ".put(" + coreClassName + "." + key + ", new Integer(" +
          name + "));";
    } else if (type.equals("long")) {
      return hashName + ".put(" + coreClassName + "." + key + ", new Long(" +
          name + "));";
    } else if (type.equals("double")) {
      return hashName + ".put(" + coreClassName + "." + key + ", new Double(" +
          name + "));";
    } else if (type.equals("String")) {
      return hashName + ".put(" + coreClassName + "." + key + ", " + name +
          ");";
    } else if (type.equals("HashName")) {
      return hashName + ".put(" + coreClassName + "." + key + ", " + name +
          ");";
    } else if (type.equals("ArrayList")) {
      return hashName + ".put(" + coreClassName + "." + key + ", " + name +
          ");";
    } else {
      System.err.println("Unknown type: " + type +
                         " in Utility.makeStringForPuttingDataToHash");
      System.exit(5);
      return null;
    }
  }

  public static String makeStringForPuttingDataToArrayList(String arrayName,
      HashMap dataInfo) {
    String type = (String) dataInfo.get("TYPE");
    String name = Utility.makeLocalVariable( (String) dataInfo.get("NAME"));
    String comment = (String) dataInfo.get("COMMENT");
    if (type.equals("boolean")) {
      return arrayName + ".add(new Boolean(" + name + "));";
    } else if (type.equals("int")) {
      return arrayName + ".add(new Integer(" + name + "));";
    } else if (type.equals("long")) {
      return arrayName + ".add(new Long(" + name + "));";
    } else if (type.equals("double")) {
      return arrayName + ".add(new Double(" + name + "));";
    } else if (type.equals("String")) {
      return arrayName + ".add(" + name + ");";
    } else if (type.equals("HashName")) {
      return arrayName + ".add(" + name + ");";
    } else if (type.equals("ArrayList")) {
      return arrayName + ".add(" + name + ");";
    } else {
      System.err.println("Unknown type: " + type +
                         " in Utility.makeStringForPuttingDataToArrayList");
      System.exit(5);
      return null;
    }
  }

  public static String makeStringForParsingToken(String type, String token) {
    String result = "";
    if (type.equals("boolean")) {
      return "Boolean.valueOf(" + token + ")";
    } else if (type.equals("int")) {
      return "Integer.valueOf(" + token + ")";
    } else if (type.equals("long")) {
      return "Long.valueOf(" + token + ")";
    } else if (type.equals("double")) {
      return "Double.valueOf(" + token + ")";
    } else if (type.equals("String")) {
      return token;
    } else {
      System.err.println("Unknown type: " + type +
                         " in Utility.makeStringForParsingToken");
      System.exit(5);
      return null;
    }
  }

}
