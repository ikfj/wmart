package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


public class UCCServerStatusNet extends UCServerStatusCore implements
    IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /** コンストラクタ */
  UCCServerStatusNet() {
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
        fData.put(UCServerStatusCore.INT_DATE, Integer.valueOf(fIn.readLine())); // 現在の日付（U-Mart暦)
        fData.put(UCServerStatusCore.INT_BOARD_NO, Integer.valueOf(fIn.readLine())); // 現在の板寄せ回数
        fData.put(UCServerStatusCore.INT_STATE, Integer.valueOf(fIn.readLine())); // 時間帯
        String okMsg = fIn.readLine();
        if (!okMsg.equals("+OK")) {
          System.err.println("Unexpected token in UCCServerStatusNet: " + okMsg);
          System.exit(5);
        }
      } else if (token.equals("+ERROR")) {
        fCommandStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fCommandStatus.setErrorCode(errCode);
        fCommandStatus.setErrorMessage(fIn.readLine());
        String okMsg = fIn.readLine();
        if (!okMsg.equals("+OK")) {
          System.err.println("Unexpected token in UCCServerStatusNet: " + okMsg);
          System.exit(5);
        }
      } else {
        System.err.println("Unexpected token in UCCServerStatusNet: " + token);
        System.exit(5);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
    return fCommandStatus;
  }

}
