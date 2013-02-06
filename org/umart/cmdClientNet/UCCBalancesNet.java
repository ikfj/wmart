package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * UCCBalancesNetクラスは, ネットワーク版クライアントにおいて実行される
 * Balancesコマンドである.
 * @author 川部 祐司
 */
public class UCCBalancesNet extends UCBalancesCore implements IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   * Constructor for UCCBalancesNet.
   */
  public UCCBalancesNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fTodayInfo.clear();
      fYesterdayInfo.clear();
      fOut.println(CMD_NAME);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        fTodayInfo.put(LONG_CASH, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_MARGIN, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_UNREALIZED_PROFIT, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_SETTLED_PROFIT, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_FEE, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_INTEREST, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_LOAN, Long.valueOf(fIn.readLine()));
        fTodayInfo.put(LONG_SURPLUS, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_CASH, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_MARGIN, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_UNREALIZED_PROFIT, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_SETTLED_PROFIT, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_FEE, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_INTEREST, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_LOAN, Long.valueOf(fIn.readLine()));
        fYesterdayInfo.put(LONG_SURPLUS, Long.valueOf(fIn.readLine()));
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
      } else {
        System.err.println(
            "Unexpected token in UCCBalancesNet: " + token);
        System.exit(5);
      }
      String okMsg = fIn.readLine();
      if (!okMsg.equals("+OK")) {
        System.err.println(
            "Unexpected token in UCCBalancesNet: " + okMsg);
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
