package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


public class UCCAccountHistoryNet extends UCAccountHistoryCore implements
    IClientCmdNet {

  /** �T�[�o�[����̓��̓X�g���[�� */
  private BufferedReader fIn;

  /** �T�[�o�[�ւ̏o�̓X�g���[�� */
  private PrintWriter fOut;

  /** �R���X�g���N�^ */
  UCCAccountHistoryNet() {
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
      fArray.clear();
      fOut.println(CMD_NAME + " " + fTargetUserId + " " + fNoOfDays);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fCommandStatus.setStatus(true);
        String s = null;
        while (! (s = fIn.readLine()).equals("+OK")) {
          HashMap data = new HashMap();
          data.put(UCAccountHistoryCore.STRING_NAME, s); // ���[�U�[��
          data.put(UCAccountHistoryCore.INT_USER_ID,
                   Integer.valueOf(fIn.readLine())); // ���[�U�[ID
          data.put(UCAccountHistoryCore.INT_STEP, Integer.valueOf(fIn.readLine())); // �X�e�b�v
          data.put(UCAccountHistoryCore.LONG_UNREALIZED_PROFIT,
                   Long.valueOf(fIn.readLine())); // ���������v
          data.put(UCAccountHistoryCore.LONG_SELL_POSITION,
                   Long.valueOf(fIn.readLine())); // ���|�W�V�����̍��v
          data.put(UCAccountHistoryCore.LONG_BUY_POSITION,
                   Long.valueOf(fIn.readLine())); // ���|�W�V�����̍��v
          fArray.add(data);
        }
      } else if (token.equals("+ERROR")) {
        fCommandStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fCommandStatus.setErrorCode(errCode);
        fCommandStatus.setErrorMessage(fIn.readLine());
        String okMsg = fIn.readLine();
        if (!okMsg.equals("+OK")) {
          System.err.println("Unexpected token in UCCAccountHistoryNet: " +
                             okMsg);
          System.exit(5);
        }
      } else {
        System.err.println("Unexpected token in UCCAccountHistoryNet: " + token);
        System.exit(5);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
    return fCommandStatus;
  }

}
