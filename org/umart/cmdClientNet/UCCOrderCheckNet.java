/*
 * Created on 2003/06/10
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * @author isao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UCCOrderCheckNet extends UCOrderCheckCore implements IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   *
   */
  public UCCOrderCheckNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /* (non-Javadoc)
   * @see org.umart.cmdClientNet.IClientCmdNet#setConnection(java.io.BufferedReader, java.io.PrintWriter)
   */
  public void setConnection(BufferedReader br, PrintWriter pw) {
    fIn = br;
    fOut = pw;
  }

  /* (non-Javadoc)
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fData.clear();
      fOut.println(CMD_NAME + " " + fOrderID);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fCommandStatus.setStatus(true);
        fData.put(UCOrderCheckCore.INT_USER_ID, Integer.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.LONG_ORDER_ID, Long.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.STRING_ORDER_TIME, fIn.readLine());
        fData.put(UCOrderCheckCore.STRING_BRAND_NAME, fIn.readLine());
        fData.put(UCOrderCheckCore.INT_ORDER_DATE, Integer.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.INT_ORDER_BOARD_NO,
                  Integer.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.INT_NEW_REPAY, Integer.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.INT_SELL_BUY, Integer.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.INT_MARKET_LIMIT,
                  Integer.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.LONG_ORDER_PRICE, Long.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.LONG_ORDER_VOLUME, Long.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.LONG_SUM_OF_CONTRACT_VOLUME,
                  Long.valueOf(fIn.readLine()));
        fData.put(UCOrderCheckCore.LONG_CANCEL_VOLUME,
                  Long.valueOf(fIn.readLine()));
        ArrayList contractInfoArray = new ArrayList();
        fData.put(UCOrderCheckCore.ARRAYLIST_CONTRACT_INFORMATION_ARRAY,
                  contractInfoArray);
        String s = null;
        while (! (s = fIn.readLine()).equals("+OK")) {
          HashMap contractInfo = new HashMap();
          contractInfo.put(UCOrderCheckCore.LONG_CONTRACT_ID, Long.valueOf(s));
          contractInfo.put(UCOrderCheckCore.STRING_CONTRACT_TIME, fIn.readLine());
          contractInfo.put(UCOrderCheckCore.INT_CONTRACT_DATE,
                           Integer.valueOf(fIn.readLine()));
          contractInfo.put(UCOrderCheckCore.INT_CONTRACT_BOARD_NO,
                           Integer.valueOf(fIn.readLine()));
          contractInfo.put(UCOrderCheckCore.LONG_CONTRACT_PRICE,
                           Long.valueOf(fIn.readLine()));
          contractInfo.put(UCOrderCheckCore.LONG_CONTRACT_VOLUME,
                           Long.valueOf(fIn.readLine()));
          contractInfoArray.add(contractInfo);
        }
      } else if (token.equals("+ERROR")) {
        fCommandStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fCommandStatus.setErrorCode(errCode);
        fCommandStatus.setErrorMessage(fIn.readLine());
        String okMsg = fIn.readLine();
        if (!okMsg.equals("+OK")) {
          System.err.println("Unexpected token in UCCScheduleNet: " + okMsg);
          System.exit(5);
        }
      } else {
        System.err.println("Unexpected token in UCCOrderCheckNet: " + token);
        System.exit(5);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(5);
    }
    return fCommandStatus;
  }

}
