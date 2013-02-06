package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


public class UCCExchangeProfileNet extends UCExchangeProfileCore implements
    IClientCmdNet {

  /** �T�[�o�[����̓��̓X�g���[�� */
  private BufferedReader fIn;

  /** �T�[�o�[�ւ̏o�̓X�g���[�� */
  private PrintWriter fOut;

  /** �R���X�g���N�^ */
  UCCExchangeProfileNet() {
    super();
    fIn = null;
    fOut = null;
  }

  public void setConnection(BufferedReader br, PrintWriter pw) {
    fIn = br;
    fOut = pw;
  }

  public UCommandStatus doIt() {
    try {
      fData.clear();
      fOut.println(CMD_NAME);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fCommandStatus.setStatus(true);
        fData.put(UCExchangeProfileCore.LONG_CASH, Long.valueOf(fIn.readLine())); // �ۗL����
        fData.put(UCExchangeProfileCore.LONG_SELL_POSITION,
                  Long.valueOf(fIn.readLine())); // ������̊Ǘ����̔��|�W�V����
        fData.put(UCExchangeProfileCore.LONG_BUY_POSITION,
                  Long.valueOf(fIn.readLine())); // ������̊Ǘ����̔��|�W�V����
        fData.put(UCExchangeProfileCore.INT_NO_OF_MEMBERS,
                  Integer.valueOf(fIn.readLine())); // �����
        String okMsg = fIn.readLine();
        if (!okMsg.equals("+OK")) {
          System.err.println("Unexpected token in UCCExchangeProfileNet: " +
                             okMsg);
          System.exit(5);
        }
      } else if (token.equals("+ERROR")) {
        fCommandStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fCommandStatus.setErrorCode(errCode);
        fCommandStatus.setErrorMessage(fIn.readLine());
        String okMsg = fIn.readLine();
        if (!okMsg.equals("+OK")) {
          System.err.println("Unexpected token in UCCExchangeProfileNet: " +
                             okMsg);
          System.exit(5);
        }
      } else {
        System.err.println("Unexpected token in UCCExchangeProfileNet: " +
                           token);
        System.exit(5);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
    return fCommandStatus;
  }

}
