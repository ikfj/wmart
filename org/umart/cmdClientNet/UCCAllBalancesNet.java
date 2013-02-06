package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * UCCAllBalancesNetクラスは, ネットワーク版クライアントにおいて実行される
 * AllBalancesコマンドである.
 * @author 川部 祐司
 */
public class UCCAllBalancesNet extends UCAllBalancesCore implements
    IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   * Constructor for UCCAllBalancesNet.
   */
  public UCCAllBalancesNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fAllBalancesArray.clear();
      fOut.println(CMD_NAME);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        String s = "";
        while (! (s = fIn.readLine()).equals("+OK")) {
          HashMap os = new HashMap();
          os.put(STRING_NAME, s);
          os.put(LONG_CASH, Long.valueOf(fIn.readLine()));
          os.put(LONG_MARGIN, Long.valueOf(fIn.readLine()));
          os.put(LONG_UNREALIZED_PROFIT, Long.valueOf(fIn.readLine()));
          os.put(LONG_SETTLED_PROFIT, Long.valueOf(fIn.readLine()));
          os.put(LONG_FEE, Long.valueOf(fIn.readLine()));
          os.put(LONG_INTEREST, Long.valueOf(fIn.readLine()));
          os.put(LONG_LOAN, Long.valueOf(fIn.readLine()));
          os.put(LONG_SURPLUS, Long.valueOf(fIn.readLine()));
          os.put(LONG_SELL_POSITION, Long.valueOf(fIn.readLine()));
          os.put(LONG_BUY_POSITION, Long.valueOf(fIn.readLine()));
          fAllBalancesArray.add(os);
        }
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
      } else {
        System.err.println("Unexpected token in UCCAllBalancesNet: " + token);
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
