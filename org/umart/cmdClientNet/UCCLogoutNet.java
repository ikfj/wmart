/*
 * Created on 2003/06/08
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * @author isao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UCCLogoutNet extends UCLogoutCore implements IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   *
   */
  public UCCLogoutNet() {
    super();
  }

  /**
   * @see org.umart.cmdClientNet.IClientCmdNet#setConnection(BufferedReader, PrintWriter)
   */
  public void setConnection(BufferedReader br, PrintWriter pw) {
    fIn = br;
    fOut = pw;
  }

  /* (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    fStatus.setStatus(false);
    try {
      fOut.println(CMD_NAME);
      StringTokenizer st = null;
      st = new StringTokenizer(fIn.readLine());
      String msg = st.nextToken();
      if (msg.equals("+ACCEPT")) {
        fIn.readLine(); // "+OK"
        fStatus.setStatus(true);
      } else {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fIn.readLine();
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    } finally {
      return fStatus;
    }
  }

}
