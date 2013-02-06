package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * UCCBoardInformationNetクラスは, ネットワーク版クライアントにおいて実行される
 * BoardInformationコマンドである.
 * @author 川部 祐司
 */
public class UCCBoardInformationNet extends UCBoardInformationCore implements
    IClientCmdNet {

  /** サーバーからの入力ストリーム */
  private BufferedReader fIn;

  /** サーバーへの出力ストリーム */
  private PrintWriter fOut;

  /**
   * Constructor for UCCBoardInformationNet.
   */
  public UCCBoardInformationNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fBoardInfo.clear();
      fOut.println(CMD_NAME);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        fBoardInfo.put(STRING_LAST_UPDATE_TIME, fIn.readLine());
        ArrayList boardArray = new ArrayList();
        fBoardInfo.put(ARRAYLIST_BOARD, boardArray);
        st = new StringTokenizer(fIn.readLine());
        HashMap marketPriceInfo = new HashMap();
        marketPriceInfo.put(LONG_PRICE, new Long( -1));
        marketPriceInfo.put(LONG_SELL_VOLUME, Long.valueOf(st.nextToken()));
        marketPriceInfo.put(LONG_BUY_VOLUME, Long.valueOf(st.nextToken()));
        boardArray.add(marketPriceInfo);
        String s = null;
        while (! (s = fIn.readLine()).equals("+OK")) {
          HashMap limitPriceInfo = new HashMap();
          StringTokenizer ast = new StringTokenizer(s);
          limitPriceInfo.put(LONG_PRICE, Long.valueOf(ast.nextToken()));
          limitPriceInfo.put(LONG_SELL_VOLUME, Long.valueOf(ast.nextToken()));
          limitPriceInfo.put(LONG_BUY_VOLUME, Long.valueOf(ast.nextToken()));
          boardArray.add(limitPriceInfo);
        }
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
      } else {
        System.err.println("Unexpected token in UCCBoardInformationNet: " +
                           token);
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
