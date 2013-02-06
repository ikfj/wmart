package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * UCCPositionNet�N���X��, �l�b�g���[�N�ŃN���C�A���g�ɂ����Ď��s�����
 * Position�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCPositionNet extends UCPositionCore implements IClientCmdNet {

  /** �T�[�o�[����̓��̓X�g���[�� */
  private BufferedReader fIn;

  /** �T�[�o�[�ւ̏o�̓X�g���[�� */
  private PrintWriter fOut;

  /**
   * Constructor for UCCPositionNet.
   */
  public UCCPositionNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fPositionInfo.clear();
      fOut.println(CMD_NAME);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        StringTokenizer hst = new StringTokenizer(fIn.readLine());
        fPositionInfo.put(LONG_TODAY_SELL, Long.valueOf(hst.nextToken()));
        fPositionInfo.put(LONG_TODAY_BUY, Long.valueOf(hst.nextToken()));
        hst = new StringTokenizer(fIn.readLine());
        fPositionInfo.put(LONG_YESTERDAY_SELL, Long.valueOf(hst.nextToken()));
        fPositionInfo.put(LONG_YESTERDAY_BUY, Long.valueOf(hst.nextToken()));
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
      } else {
        System.err.println("Unexpected token in UCCPositionNet: " + token);
        System.exit(5);
      }
      String okMsg = fIn.readLine();
      if (!okMsg.equals("+OK")) {
        System.err.println("Unexpected token in UCCPositionNet: " + okMsg);
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
