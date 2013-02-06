/*
 * Created on 2003/06/09
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.umart.clientNet;

import java.util.*;

import org.umart.cmdClientNet.*;
import org.umart.cmdCore.*;
import org.umart.serverCore.UAgentFactory;
import org.umart.strategyCore.UBaseAgent;



/**
 * @author isao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UNetClient {

  /** 戦略オブジェクト */
  private UBaseAgent fStrategy;

  /** CProtocolオブジェクト */
  private UProtocolForNetClient fCProtocol;

  /** 運用日数 */
  private int fMaxDate;

  /** 一日当たりの板寄せ回数 */
  private int fNoOfBoardsPerDay;

  /** 現在の日付 */
  private int fDate;

  /** 現在の板寄せ回数 */
  private int fBoardNo;

  /** 現在のサーバー状態 */
  private int fServerState;
  
  /** サーバへの問い合わせ間隔 [sec] */
  private long fInterval;

  /**
   * コンストラクタ
   * @param hostname サーバーホスト名（IPアドレスでもOK）
   * @param port サーバーポート番号
   * @param org.umart.strategy 戦略オブジェクト
   */
  public UNetClient(String hostname, int port, UBaseAgent strategy, long interval) {
    super();
    fStrategy = strategy;
    fCProtocol = new UProtocolForNetClient();
    fCProtocol.setConnection(hostname, port);
    fDate = 0;
    fBoardNo = 0;
    fServerState = -1;
    fInterval = interval;
  }

  /**
   * メインループ
   */
  public void mainLoop() {
    fStrategy.setCProtocol(fCProtocol);
    login();
    setupSchedule();
    while (true) {
      waitNextBoard();
      if (fDate > fMaxDate) {
        logout();
        System.out.println("Normally terminated...");
        break;
      }
      System.out.println("Date:" + fDate + ", Board:" + fBoardNo + ", State:" +
                         fServerState);
      fStrategy.doActions(fDate, fBoardNo, fServerState, fMaxDate,
                          fNoOfBoardsPerDay);
    }
  }

  private void login() {
    String userName = fStrategy.getLoginName();
    String passwd = fStrategy.getPasswd();
    UCLoginCore loginCmd = (UCLoginCore) fCProtocol.getCommand(UCLoginCore.
        CMD_NAME);
    loginCmd.setArguments(userName, passwd);
    UCommandStatus cmdStatus = loginCmd.doIt();
    if (!cmdStatus.getStatus()) {
      System.err.println("Can't login with (" + userName + ", " + passwd + ")");
      System.exit(5);
    }
  }

  private void logout() {
    UCLogoutCore logoutCmd = (UCLogoutCore) fCProtocol.getCommand(UCLogoutCore.
        CMD_NAME);
    logoutCmd.doIt();
  }

  private void setupSchedule() {
    UCScheduleCore schCmd = (UCScheduleCore) fCProtocol.getCommand(
        UCScheduleCore.CMD_NAME);
    doCommand(schCmd);
    HashMap result = schCmd.getResults();
    fMaxDate = ( (Integer) result.get(UCScheduleCore.INT_MAX_DAY)).intValue();
    fNoOfBoardsPerDay = ( (Integer) result.get(UCScheduleCore.INT_NO_OF_BOARDS)).
        intValue();
  }

  private void doCommand(ICommand cmd) {
    UCommandStatus cmdStatus = cmd.doIt();
    while (!cmdStatus.getStatus()) {
      System.err.println("Error in doing " + cmd.getName());
      System.err.println(cmdStatus.getErrorMessage());
      System.err.println("Retrying...");
      cmdStatus = cmd.doIt();
    }
  }

  private void waitNextBoard() {
    ICommand cmd = null;
    while (true) {
      cmd = fCProtocol.getCommand(UCServerDateCore.CMD_NAME);
      doCommand(cmd);
      HashMap result = ((UCServerDateCore)cmd).getResults();
      int date = ((Integer)result.get(UCServerDateCore.INT_DAY)).intValue();
      int boardNo = ((Integer)result.get(UCServerDateCore.INT_BOARD_NO)).intValue();
      cmd = fCProtocol.getCommand(UCMarketStatusCore.CMD_NAME);
      doCommand(cmd);
      result = ((UCMarketStatusCore)cmd).getMarketInfo();
      int serverState = ((Integer)result.get(UCMarketStatusCore.INT_MARKET_STATUS)).intValue();
      if (date != fDate || boardNo != fBoardNo || fServerState != serverState) {
        fDate = date;
        fBoardNo = boardNo;
        fServerState = serverState;
        return;
      }
      try {
      	Thread.sleep(fInterval * 1000);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    if (args.length != 7 && args.length != 8) {
      System.err.print("usage: java UNetClient <serverName> <port> <userName> <passwd> ");
      System.err.println("<org.umart.strategy> <seed> <interval[sec]> [<args1:args2:...>]");
      System.exit(1);
    }
    String serverName = args[0];
    int port = Integer.parseInt(args[1]);
    String userName = args[2];
    String passwd = args[3];
    String strategyName = args[4];
    int seed = Integer.parseInt(args[5]);
    long interval = Long.parseLong(args[6]);
    String paramsStr = "";
    if (args.length == 8) {
      paramsStr = args[7];
    }
    UBaseAgent strategy = UAgentFactory.makeAgent(userName, passwd, strategyName, paramsStr, seed);
    UNetClient client = new UNetClient(serverName, port, strategy, interval);
    client.mainLoop();
  }
}
