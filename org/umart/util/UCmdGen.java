/*
 * Created on 2003/08/21
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.umart.util;

/**
 * @author isao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UCmdGen {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("usage: java UCmdClientNetGenerator cmdDefFile");
      System.exit(0);
    }
    String cmdFile = args[0];
    UCmdDefinition cmdDef = new UCmdDefinition();
    cmdDef.readFrom(cmdFile);
    UCmdClientNetGenerator cmdClientNetGen = new UCmdClientNetGenerator(cmdDef);
    UCmdClientSAGenerator cmdClientSAGen = new UCmdClientSAGenerator(cmdDef);
    UCmdCoreGenerator cmdCoreGen = new UCmdCoreGenerator(cmdDef);
    UCmdServerGenerator cmdServerGen = new UCmdServerGenerator(cmdDef);
    UServerMethodGenerator serverMethodGen = new UServerMethodGenerator(cmdDef);
    try {
      cmdClientNetGen.writeCommandClientNet();
      cmdClientSAGen.writeCommandClientSA();
      cmdCoreGen.writeCommandCore();
      cmdServerGen.writeCommandServer();
      serverMethodGen.writeServerMethod();
      System.out.println("Done.");
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
  }
}
