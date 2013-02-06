package org.umart.cmdClientNet;

import java.io.*;
import java.util.*;

import org.umart.cmdCore.*;


/**
 * UCCBoardDataNet�N���X��, �l�b�g���[�N�ŃN���C�A���g�ɂ����Ď��s�����
 * BoardData�R�}���h�ł���.
 * @author �암 �S�i
 */
public class UCCBoardDataNet extends UCBoardDataCore implements IClientCmdNet {

  /** �T�[�o�[����̓��̓X�g���[�� */
  private BufferedReader fIn;

  /** �T�[�o�[�ւ̏o�̓X�g���[�� */
  private PrintWriter fOut;

  /**
   * Constructor for UCCBoardDataNet.
   */
  public UCCBoardDataNet() {
    super();
    fIn = null;
    fOut = null;
  }

  /**
   * @see org.umart.cmdCore.ICommand#doIt()
   */
  public UCommandStatus doIt() {
    try {
      fBoardDataArray.clear();
      fBoardDataInfo.clear();
      fOut.println(CMD_NAME);
      StringTokenizer st = new StringTokenizer(fIn.readLine());
      String token = st.nextToken();
      if (token.equals("+ACCEPT")) {
        fStatus.setStatus(true);
        StringTokenizer hst = new StringTokenizer(fIn.readLine());
        fBoardDataInfo.put(LONG_TOTAL_BUY_VOLUME, Long.valueOf(hst.nextToken()));
        fBoardDataInfo.put(LONG_MIN_PRICE, Long.valueOf(hst.nextToken()));
        fBoardDataInfo.put(LONG_MAX_PRICE, Long.valueOf(hst.nextToken()));
        fBoardDataInfo.put(LONG_CONTRACT_PRICE, Long.valueOf(hst.nextToken()));
        fBoardDataInfo.put(LONG_CONTRACT_VOLUME, Long.valueOf(hst.nextToken()));
        String s = "";
        while (! (s = fIn.readLine()).equals("+OK")) {
          HashMap os = new HashMap();
          StringTokenizer ast = new StringTokenizer(s);
          os.put(LONG_PRICE, Long.valueOf(ast.nextToken()));
          os.put(STRING_SELL_BUY, ast.nextToken());
          os.put(LONG_VOLUME, Long.valueOf(ast.nextToken()));
          fBoardDataArray.add(os);
        }
      } else if (token.equals("+ERROR")) {
        fStatus.setStatus(false);
        int errCode = Integer.parseInt(st.nextToken());
        fStatus.setErrorCode(errCode);
        fStatus.setErrorMessage(fIn.readLine());
        String s = fIn.readLine();
        if (!s.equals("+OK")) {
          System.err.println("Unexpected token in UCCBoardDataNet: " + s);
          System.exit(5);
        }
      } else {
        System.err.println("Unexpected token in UCCBoardDataNet: " + token);
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
