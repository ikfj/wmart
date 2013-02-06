package org.wmart.cmdCore;

import java.util.*;

/**
 * コマンドを定義するためのインターフェースです．
 * システム中の全てのコマンドはこのインターフェースを実装しなければなりません．
 * @author 小野　功
 */
public interface ICommand {

  /** 引数が間違っていることを表す定数 */
  public static final int INVALID_ARGUMENTS = 1;

  /** 受付られないコマンドであることを表す定数 */
  public static final int UNACCEPTABLE_COMMAND = 2;

  /** 存在しないコマンドであることを表す定数 */
  public static final int INVALID_COMMAND = 3;

  /**
   * コマンド名を返す．
   * @param コマンド名
   */
  public String getName();

  /**
   * このコマンドの名前がnameと等しいか？
   * @param name 比較するコマンド名
   * @return true:等しい，false:等しくない
   */
  public boolean isNameEqualTo(String name);

  /**
   * このコマンドに必要な引数を設定する．
   * @param st 引数群（StringTokenizer.nextTokenで取り出せる）
   * return true：引数を正しく設定できた場合，false:失敗した場合
   */
  public boolean setArguments(StringTokenizer st);

  /**
   * コマンドを実行する．
   * @return 実行結果
   */
  public WCommandStatus doIt();

  /**
   * コマンドの実行結果を標準出力に表示する．
   */
  public void printOn();

  /**
   * コマンドの実行結果をスペース区切りの文字列として返す．
   * @return 実行結果の文字列
   */
  public String getResultString();

}
