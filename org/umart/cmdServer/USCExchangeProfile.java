package org.umart.cmdServer;

import org.umart.cmdCore.*;
import org.umart.serverNet.*;


/**
 * サーバー上においてUAgentにより実行される取引所情報を取得するためのSVMPコマンドクラスです．
 * 取引所情報とは，取引所の保有現金，取引所の管理下の売ポジション，買ポジション，会員数のことです．
 * @author 小野　功
 */
public class USCExchangeProfile extends UCExchangeProfileCore implements
    IServerCmd {

  /** このコマンドを実行したエージェント */
  private UAgentForNetworkClient fAgent;

  /** サーバーへの参照 */
  UMartNetwork fUMart;

  /** コンストラクタ */
  public USCExchangeProfile() {
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
      fCommandStatus = fUMart.doExchangeProfile(fData, userID);
      if (fCommandStatus.getStatus()) {
        fAgent.sendMessage("+ACCEPT");
        fAgent.sendMessage(fData.get(UCExchangeProfileCore.LONG_CASH).toString()); // 保有現金
        fAgent.sendMessage(fData.get(UCExchangeProfileCore.LONG_SELL_POSITION).toString()); // 取引所の管理下の売ポジション
        fAgent.sendMessage(fData.get(UCExchangeProfileCore.LONG_BUY_POSITION).toString()); // 取引所の管理下の買ポジション
        fAgent.sendMessage(fData.get(UCExchangeProfileCore.INT_NO_OF_MEMBERS).toString()); // 会員数
      } else {
        fAgent.sendMessage("+ERROR " + fCommandStatus.getErrorCode());
        fAgent.sendMessage(fCommandStatus.getErrorMessage());
      }
    } catch (Exception e) {
      fAgent.sendMessage("+ERROR " + ICommand.INVALID_ARGUMENTS);
      fAgent.sendMessage("USAGE: EXCHANGEPROFILE");
    }
    fAgent.flushMessage();
    return fCommandStatus;
  }

}
