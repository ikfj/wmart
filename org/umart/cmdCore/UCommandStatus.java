package org.umart.cmdCore;

import java.io.*;
import java.util.*;

/**
   UCommandStatusクラスは, コマンドのサーバーでの実行結果の状態を
   保持するクラスである.
   <br>
   @author 小野 功
 */

public class UCommandStatus {

  /** 実行結果の状態 (true: 成功, false: 失敗) */
  private boolean fStatus;

  /** エラーコード */
  private int fErrorCode;

  /** エラーメッセージ */
  private String fErrorMessage;

  public UCommandStatus() {
  }

  /**
     brから実行結果状態を読み込み, 解析する.
     @param br ストリーム
   */
  public void readFrom(BufferedReader br) throws IOException {
    String line = br.readLine();
    StringTokenizer st = new StringTokenizer(line);
    String token = st.nextToken();
    if (token.equals("+ACCEPT")) {
      fStatus = true;
      return;
    } else if (token.equals("+ERROR")) {
      token = st.nextToken();
      fErrorCode = Integer.parseInt(token);
      fErrorMessage = br.readLine();
    } else {
      System.err.println("Error in UCommandStatus.readFrom");
      System.exit(5);
    }
  }

  /**
     実行結果の状態を設定する.
     @param status 実行結果の状態
   */
  public void setStatus(boolean status) {
    fStatus = status;
  }

  /**
     実行結果の状態を返す.
     @return 実行結果の状態
   */
  public boolean getStatus() {
    return fStatus;
  }

  /**
     エラーコードを設定する.
     @param code エラーコード
   */
  public void setErrorCode(int code) {
    fErrorCode = code;
  }

  /**
     エラーコードを返す.
     @return エラーコード
   */
  public int getErrorCode() {
    return fErrorCode;
  }

  /**
     エラーメッセージを設定する.
     @param msg エラーメッセージ
   */
  public void setErrorMessage(String msg) {
    fErrorMessage = msg;
  }

  /**
     エラーメッセージを返す.
     @return エラーメッセージ
   */
  public String getErrorMessage() {
    return fErrorMessage;
  }

}
