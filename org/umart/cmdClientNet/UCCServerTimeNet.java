package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * UCCServerTimeNet�N���X��, �l�b�g���[�N�ŃN���C�A���g�ɂ����Ď��s�����
 * ServerTime�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCServerTimeNet extends UCServerTimeCore implements IClientCmdNet {

  /** �T�[�o�[����̓��̓X�g���[�� */
  private BufferedReader fIn;

  /** �T�[�o�[�ւ̏o�̓X�g���[�� */
  private PrintWriter fOut;

  /**
   * Constructor for UCCServerTimeNet.
   */
  public UCCServerTimeNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fServerTimeInfo.clear();
      fOut.println(CMD_NAME);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        StringTokenizer hst = new StringTokenizer(fIn.readLine());
        fServerTimeInfo.put(STRING_SERVER_TIME, hst.nextToken());
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
      } else {
        System.err.println("Unexpected token in UCCServerTimeNet: " + token);
        System.exit(5);
      }
      String okMsg = fIn.readLine();
      if (!okMsg.equals("+OK")) {
        System.err.println("Unexpected token in UCCServerTimeNet: " + okMsg);
        System.exit(5);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
    return fStatus;
  }

  /**
   * @see org.umart.cmdClientNet.IClientCmdNet#setConnection(BufferedReader, PrintWriter)
   */
  public void setConnection(BufferedReader br, PrintWriter pw) {
    fIn = br;
    fOut = pw;
  }
}
