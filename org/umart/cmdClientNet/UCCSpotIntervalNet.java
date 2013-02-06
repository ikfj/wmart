package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * UCCSpotIntervalNet�N���X��, �l�b�g���[�N�ŃN���C�A���g�ɂ����Ď��s�����
 * SpotInterval�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCSpotIntervalNet extends UCSpotIntervalCore implements
    IClientCmdNet {

  /** �T�[�o�[����̓��̓X�g���[�� */
  private BufferedReader fIn;

  /** �T�[�o�[�ւ̏o�̓X�g���[�� */
  private PrintWriter fOut;

  /**
   * Constructor for UCCSpotIntervalNet.
   */
  public UCCSpotIntervalNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fIntervalInfo.clear();
      fOut.println(CMD_NAME);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        fIntervalInfo.put(
            INT_SPOT_INTERVAL,
            Integer.valueOf(fIn.readLine()));
      } else {
        System.err.println(
            "Unexpected token in UCCSpotIntervalNet: " + token);
        System.exit(5);
      }
      String okMsg = fIn.readLine();
      if (!okMsg.equals("+OK")) {
        System.err.println(
            "Unexpected token in UCCSpotIntervalNet: " + okMsg);
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
