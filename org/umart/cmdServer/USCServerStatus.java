package org.umart.cmdServer;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


public class USCServerStatus extends UCServerStatusCore implements IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  UMartNetwork fUMart;

  /** コンストラクタ */
  public USCServerStatus() {
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
      fData.clear();
      fCommandStatus = fUMart.doServerStatus(fData, userID);
      if (fCommandStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fData.get(UCServerStatusCore.INT_DATE).toString()); // 現在の日付（U-Mart暦)
        fAgent.sendMessage(fData.get(UCServerStatusCore.INT_BOARD_NO).toString()); // 現在の板寄せ回数
        fAgent.sendMessage(fData.get(UCServerStatusCore.INT_STATE).toString()); // 時間帯
      } else {
        fAgent.sendMessage("+ERROR " + fCommandStatus.getErrorCode());
        fAgent.sendMessage(fCommandStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: SERVERSTATUS");
    }
    fAgent.flushMessage();
    return fCommandStatus;
  }

}
