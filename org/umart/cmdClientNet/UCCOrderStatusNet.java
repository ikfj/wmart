package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * UCCOrderStatusNetクラスは, ネットワーク版クライアントにおいて実行される
 * OrderStatusコマンドである.
 * @author 川部 祐司
 */
public class UCCOrderStatusNet extends UCOrderStatusCore implements
    IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   * Constructor for UCCOrderStatusNet.
   */
  public UCCOrderStatusNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fStatusArray.clear();
      fOut.println(CMD_NAME);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        while (true) {
          HashMap os = new HashMap();
          String s = fIn.readLine();
          if (s.equals("+OK")) {
            break;
          }
          os.put(LONG_ORDER_ID, Long.valueOf(s));
          os.put(STRING_ORDER_TIME, fIn.readLine());
          os.put(STRING_BRAND_NAME, fIn.readLine());
          os.put(INT_NEW_REPAY, Integer.valueOf(fIn.readLine()));
          os.put(INT_SELL_BUY, Integer.valueOf(fIn.readLine()));
          os.put(INT_MARKET_LIMIT, Integer.valueOf(fIn.readLine()));
          os.put(LONG_PRICE, Long.valueOf(fIn.readLine()));
          os.put(LONG_VOLUME, Long.valueOf(fIn.readLine()));
          os.put(LONG_CONTRACTED_VOLUME, Long.valueOf(fIn.readLine()));
          os.put(LONG_CANCEL_VOLUME, Long.valueOf(fIn.readLine()));
          fStatusArray.add(os);
        }
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
      } else {
        System.err.println(
            "Unexpected token in UCCOrderStatusNet: " + token);
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
