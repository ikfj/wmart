package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


public class UCCAccountHistoryNet extends UCAccountHistoryCore implements
    IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /** コンストラクタ */
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
          data.put(UCAccountHistoryCore.STRING_NAME, s); // ユーザー名
          data.put(UCAccountHistoryCore.INT_USER_ID,
                   Integer.valueOf(fIn.readLine())); // ユーザーID
          data.put(UCAccountHistoryCore.INT_STEP, Integer.valueOf(fIn.readLine())); // ステップ
          data.put(UCAccountHistoryCore.LONG_UNREALIZED_PROFIT,
                   Long.valueOf(fIn.readLine())); // 未実現利益
          data.put(UCAccountHistoryCore.LONG_SELL_POSITION,
                   Long.valueOf(fIn.readLine())); // 買ポジションの合計
          data.put(UCAccountHistoryCore.LONG_BUY_POSITION,
                   Long.valueOf(fIn.readLine())); // 売ポジションの合計
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
