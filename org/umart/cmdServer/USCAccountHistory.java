package org.umart.cmdServer;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


public class USCAccountHistory extends UCAccountHistoryCore implements
    IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  UMartNetwork fUMart;

  /** コンストラクタ */
  public USCAccountHistory() {
    super();
    fAgent = null;
    fUMart = null;
  }

  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart) {
    fAgent = agent;
    fUMart = umart;
  }

  public UCommandStatus doIt() {
    try {
      int userID = fAgent.getLoginStatus().getUserID();
      fArray.clear();
      fCommandStatus = fUMart.doAccountHistory(fArray, userID, fTargetUserId,
                                               fNoOfDays);
      if (fCommandStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        Iterator itr = fArray.iterator();
        while (itr.hasNext()) {
          HashMap data = (HashMap) itr.next();
          fAgent.sendMessage(data.get(UCAccountHistoryCore.STRING_NAME).
                             toString()); // ユーザー名
          fAgent.sendMessage(data.get(UCAccountHistoryCore.INT_USER_ID).
                             toString()); // ユーザーID
          fAgent.sendMessage(data.get(UCAccountHistoryCore.INT_STEP).toString()); // ステップ
          fAgent.sendMessage(data.get(UCAccountHistoryCore.
                                      LONG_UNREALIZED_PROFIT).toString()); // 未実現利益
          fAgent.sendMessage(data.get(UCAccountHistoryCore.LONG_SELL_POSITION).
                             toString()); // 買ポジションの合計
          fAgent.sendMessage(data.get(UCAccountHistoryCore.LONG_BUY_POSITION).
                             toString()); // 売ポジションの合計
        }
      } else {
        fAgent.sendMessage("+ERROR " + fCommandStatus.getErrorCode());
        fAgent.sendMessage(fCommandStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: ACCOUNTHISTORY");
    }
    fAgent.flushMessage();
    return fCommandStatus;
  }

}
