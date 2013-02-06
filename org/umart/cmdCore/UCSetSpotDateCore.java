package org.umart.cmdCore;

import java.util.*;

/**
 * UCSetScheduleCoreクラスは，現物価格系列開始ポイント設定のためのコマンドオブジェクトである．
 * @author 小野　功
 */
public abstract class UCSetSpotDateCore implements ICommand {

  /** コマンド名 */
  public static final String CMD_NAME = "SetSpotDate";

  /** 別名 */
  public static final String CMD_ALIAS = "";

  /** コマンドの実行状態 */
  protected UCommandStatus fStatus;

  /** 現物価格系列開始ポイント */
  protected int fStartPoint;

  /**
   * Constructor for UCSetSpotDateCore.
   */
  public UCSetSpotDateCore() {
    super();
    fStatus = new UCommandStatus();
    fStartPoint = -1;
  }

  /**
   * 	@see org.umart.cmdCore.ICommand#isNameEqualTo(String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME) || name.equals(CMD_ALIAS)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @see org.umart.cmdCore.ICommand#getName()
   */
  public String getName() {
    return CMD_NAME;
  }

  /**
   * @see org.umart.cmdCore.ICommand#setArguments(StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    try {
      fStartPoint = Integer.valueOf(st.nextToken()).intValue();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 	コマンド実行に必要な引数を設定する．
   * @param startPoint 現物価格系列開始ポイント
   */
  public void setArguments(int startPoint) {
    fStartPoint = startPoint;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public abstract UCommandStatus doIt();

  /**
   * @see org.umart.cmdCore.ICommand#printOn()
   */
  public void printOn() {
    System.out.println("<<" + CMD_NAME + " " + fStartPoint + ">>");
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = "";
    return result;
  }

}
