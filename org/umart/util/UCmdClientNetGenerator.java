/*
 * Created on 2003/08/02
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.umart.util;

import java.io.*;
import java.util.*;

/**
 * @author isao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UCmdClientNetGenerator {

  /** �R�}���h��` */
  private UCmdDefinition fCmdDef;

  /** "+OK" ���ǂݍ��܂ꂽ���ǂ�����\���t���O */
  private boolean fReadOkFlag;

  /**
   * �R���X�g���N�^
   *
   */
  public UCmdClientNetGenerator(UCmdDefinition cmdDef) {
    fCmdDef = cmdDef;
    fReadOkFlag = false;
  }

  private void writeDataClear(PrintWriter pw, String indent) {
    String str = indent;
    str += Utility.makeFieldString( (String) fCmdDef.getReturnData().get("NAME"));
    str += ".clear();";
    pw.println(str);
  }

  private void writeSendingCommand(PrintWriter pw, String indent) {
    String str = indent + "fOut.println(CMD_NAME";
    Iterator itr = fCmdDef.getArgList().iterator();
    while (itr.hasNext()) {
      HashMap argInfo = (HashMap) itr.next();
      String argName = Utility.makeFieldString( (String) argInfo.get("NAME"));
      str += " + \" \" + " + argName;
    }
    str += ");";
    pw.println(str);
  }

  private void writeReadingHashMap(PrintWriter pw, String indent, HashMap item,
                                   String hashName) {
    String coreClassName = "UC" + fCmdDef.getCmdName() + "Core";
    String type = (String) item.get("TYPE");
    ArrayList contents = (ArrayList) item.get("CONTENTS");
    Iterator itr = contents.iterator();
    while (itr.hasNext()) {
      HashMap item2 = (HashMap) itr.next();
      String type2 = (String) item2.get("TYPE");
      String name2 = (String) item2.get("NAME");
      String key2 = Utility.makeKeyString(type2, name2);
      String comment2 = (String) item2.get("COMMENT");
      String localVariableName = Utility.makeLocalVariable(name2);
      if (type2.equals("HashMap")) {
        pw.print(indent + "HashMap " + localVariableName + " = new HashMap();");
        pw.println(" // " + comment2);
        writeReadingHashMap(pw, indent, item2, localVariableName);
        pw.println(indent + hashName + ".put(" + key2 + ", " +
                   localVariableName + ");");
      } else if (type2.equals("ArrayList")) {
        pw.print(indent + "ArrayList " + localVariableName +
                 " = new ArrayList();");
        pw.println(" // " + comment2);
        writeReadingArrayList(pw, indent, item2, localVariableName);
        pw.println(indent + hashName + ".put(" + key2 + ", " +
                   localVariableName + ");");
      } else {
        String str = indent + hashName + ".put(" + coreClassName + "." + key2;
        str += ", " + Utility.makeStringForParsingToken(type2, "fIn.readLine()") +
            ");";
        pw.print(str);
        pw.println(" // " + comment2);
      }
    }
  }

  private void writeReadingArrayList(PrintWriter pw, String indent,
                                     HashMap item, String arrayName) {
    String coreClassName = "UC" + fCmdDef.getCmdName() + "Core";
    String type = (String) item.get("TYPE");
    HashMap contents = (HashMap) item.get("CONTENTS");
    String name = Utility.makeLocalVariable( (String) item.get("NAME"));
    String type2 = (String) contents.get("TYPE");
    String name2 = Utility.makeLocalVariable( (String) contents.get("NAME"));
    String comment2 = (String) contents.get("COMMENT");
    pw.println(indent + "String s = null;");
    if (fReadOkFlag) {
      System.err.println("Error: ArrayList is detected more than twice!!");
      System.exit(5);
    }
    pw.println(indent + "while ( !(s = fIn.readLine()).equals(\"+OK\") ) {");
    fReadOkFlag = true;
    if (type2.equals("HashMap")) {
      pw.println(indent + "  HashMap " + name2 + " = new HashMap();");
      writeReadingHashMap(pw, indent + "  ", contents, name2);
      pw.println(indent + "  " + arrayName + ".add(" + name2 + ");");
    } else if (type2.equals("ArrayList")) {
      System.err.println("Error: ArrayList cannot be nested!!");
      System.exit(5);
//			pw.print(indent + "  ArrayList " + name2 + " = new ArrayList();");
//			pw.println("  // " + comment2);
//			writeReadingArrayList(pw, indent, contents, name);
//			pw.println(indent + "  " + arrayName + ".add(" + name2 + ");");
    } else {
      String str = indent + "  " + arrayName + ".add(";
      str += Utility.makeStringForParsingToken(type2, "s") + ");";
      pw.print(str);
      pw.println(" // " + comment2);
    }
    pw.println(indent + "}");
  }

  public void writeCommandClientNet() throws FileNotFoundException {
    String className = "UCC" + fCmdDef.getCmdName() + "Net";
    PrintWriter pw = new PrintWriter(new FileOutputStream(className + ".java"));
    System.out.println("Wrting " + className + ".java ...");
    pw.println("package org.umart.cmdClientNet;");
    pw.println();
    pw.println("import java.io.BufferedReader;");
    pw.println("import java.io.PrintWriter;");
    pw.println("import java.util.ArrayList;");
    pw.println("import java.util.HashMap;");
    pw.println("import java.util.Iterator;");
    pw.println("import java.util.StringTokenizer;");
    pw.println("import org.umart.serverNet.UAgent;");
    pw.println("import org.umart.serverNet.UMartNetwork;");
    pw.println("import org.umart.cmdCore.ICommand;");
    String coreClassName = "UC" + fCmdDef.getCmdName() + "Core";
    pw.println("import org.umart.cmdCore." + coreClassName + ";");
    pw.println("import org.umart.cmdCore.UCommandStatus;");
    pw.println();
    pw.println("public class " + className + " extends UC"
               + fCmdDef.getCmdName() + "Core implements IClientCmdNet {");
    pw.println();
    pw.println("  /** �T�[�o�[����̓��̓X�g���[�� */");
    pw.println("  private BufferedReader fIn;");
    pw.println();
    pw.println("  /** �T�[�o�[�ւ̏o�̓X�g���[�� */");
    pw.println("  private PrintWriter fOut;");
    pw.println();
    pw.println("  /** �R���X�g���N�^ */");
    pw.println("  " + className + "() {");
    pw.println("    super();");
    pw.println("    fIn = null;");
    pw.println("    fOut = null;");
    pw.println("  }");
    pw.println();
    pw.println(
        "  public void setConnection(BufferedReader br, PrintWriter pw) {");
    pw.println("    fIn = br;");
    pw.println("    fOut = pw;");
    pw.println("  }");
    pw.println();
    pw.println("  public UCommandStatus doIt() {");
    pw.println("    try {");
    writeDataClear(pw, "      ");
    writeSendingCommand(pw, "      ");
    pw.println(
        "      StringTokenizer st = new StringTokenizer(fIn.readLine());");
    pw.println("      String token = st.nextToken();");
    pw.println("      if ( token.equals(\"+ACCEPT\") ) {");
    pw.println("        fCommandStatus.setStatus(true);");
    String type = (String) fCmdDef.getReturnData().get("TYPE");
    String fieldName = Utility.makeFieldString( (String) fCmdDef.getReturnData().
                                               get("NAME"));
    if (type.equals("HashMap")) {
      writeReadingHashMap(pw, "        ", fCmdDef.getReturnData(), fieldName);
    } else if (type.equals("ArrayList")) {
      writeReadingArrayList(pw, "        ", fCmdDef.getReturnData(), fieldName);
    }
    if (!fReadOkFlag) {
      pw.println("        String okMsg = fIn.readLine();");
      pw.println("        if ( !okMsg.equals(\"+OK\") ) {");
      pw.println("          System.err.println(\"Unexpected token in " +
                 className + ": \" + okMsg);");
      pw.println("          System.exit(5);");
      pw.println("        }");
    }
    pw.println("      } else if ( token.equals(\"+ERROR\") ) {");
    pw.println("        fCommandStatus.setStatus(false);");
    pw.println("        int errCode = Integer.parseInt(st.nextToken());");
    pw.println("        fCommandStatus.setErrorCode(errCode);");
    pw.println("        fCommandStatus.setErrorMessage(fIn.readLine());");
    pw.println("        String okMsg = fIn.readLine();");
    pw.println("        if ( !okMsg.equals(\"+OK\") ) {");
    pw.println("          System.err.println(\"Unexpected token in " +
               className + ": \" + okMsg);");
    pw.println("          System.exit(5);");
    pw.println("        }");
    pw.println("      } else {");
    pw.println("        System.err.println(\"Unexpected token in " + className +
               ": \" + token);");
    pw.println("        System.exit(5);");
    pw.println("      }");
    pw.println("    } catch (Exception e) {");
    pw.println("      e.printStackTrace();");
    pw.println("      System.exit(5);");
    pw.println("    }");
    pw.println("    return fCommandStatus;");
    pw.println("  }");
    pw.println();
    pw.println("}");
    pw.close();
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("usage: java UCmdClientNetGenerator cmdfile");
      System.exit(0);
    }
    String cmdFile = args[0];
    UCmdDefinition cmdDef = new UCmdDefinition();
    cmdDef.readFrom(cmdFile);
    UCmdClientNetGenerator gen = new UCmdClientNetGenerator(cmdDef);
    try {
      gen.writeCommandClientNet();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
  }

}
