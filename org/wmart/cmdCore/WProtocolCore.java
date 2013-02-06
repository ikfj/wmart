package org.wmart.cmdCore;

import java.util.*;

/**
 * SVMPコマンドオブジェクト集合を保持するプロトコルクラスです．
 * SVMPコマンド名をキーとして使って適切なSVMPオブジェクトを取得することができます．
 * @author 小野　功
 */
public abstract class WProtocolCore {

	/** SVMPコマンドオブジェクトのデータベース */
  protected HashMap fCommandHash;

  /**
   *コンストラクタ
   */
  public WProtocolCore() {
    fCommandHash = new HashMap();
  }

  /**
   * SVMPコマンドオブジェクトのデータベースを返す．
   * @return SVMPコマンドオブジェクトのデータベース(HashMap)
   */
  public HashMap getCommandHashMap() {
    return fCommandHash;
  }

  /**
   * コマンド名がcmdStrのSVMPコマンドオブジェクトを返す．
   * @param cmdStr コマンド名
   * @return SVMPコマンドオブジェクト
   */
  public ICommand getCommand(String cmdStr) {
    return (ICommand)fCommandHash.get(cmdStr);
  }
}
