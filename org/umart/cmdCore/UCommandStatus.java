package org.umart.cmdCore;

import java.io.*;
import java.util.*;

/**
   UCommandStatus�N���X��, �R�}���h�̃T�[�o�[�ł̎��s���ʂ̏�Ԃ�
   �ێ�����N���X�ł���.
   <br>
   @author ���� ��
 */

public class UCommandStatus {

  /** ���s���ʂ̏�� (true: ����, false: ���s) */
  private boolean fStatus;

  /** �G���[�R�[�h */
  private int fErrorCode;

  /** �G���[���b�Z�[�W */
  private String fErrorMessage;

  public UCommandStatus() {
  }

  /**
     br������s���ʏ�Ԃ�ǂݍ���, ��͂���.
     @param br �X�g���[��
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
     ���s���ʂ̏�Ԃ�ݒ肷��.
     @param status ���s���ʂ̏��
   */
  public void setStatus(boolean status) {
    fStatus = status;
  }

  /**
     ���s���ʂ̏�Ԃ�Ԃ�.
     @return ���s���ʂ̏��
   */
  public boolean getStatus() {
    return fStatus;
  }

  /**
     �G���[�R�[�h��ݒ肷��.
     @param code �G���[�R�[�h
   */
  public void setErrorCode(int code) {
    fErrorCode = code;
  }

  /**
     �G���[�R�[�h��Ԃ�.
     @return �G���[�R�[�h
   */
  public int getErrorCode() {
    return fErrorCode;
  }

  /**
     �G���[���b�Z�[�W��ݒ肷��.
     @param msg �G���[���b�Z�[�W
   */
  public void setErrorMessage(String msg) {
    fErrorMessage = msg;
  }

  /**
     �G���[���b�Z�[�W��Ԃ�.
     @return �G���[���b�Z�[�W
   */
  public String getErrorMessage() {
    return fErrorMessage;
  }

}
