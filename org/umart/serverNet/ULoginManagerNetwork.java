package org.umart.serverNet;

import java.net.*;

import org.umart.serverCore.ULoginManager;

/**
 * �l�b�g���[�N�z���Ƀ��O�C������G�[�W�F���g��������悤��ULoginManager���@�\�g�������N���X�ł��D
 * @author ����@��
 */
public class ULoginManagerNetwork extends ULoginManager implements Runnable {

  /** �N���C�A���g����̐ڑ��v�����󂯕t���邽�߂̃T�[�o�[�\�P�b�g */
  private ServerSocket fServerSocket;

  /** �X���b�h�I�u�W�F�N�g */
  private Thread fThread;

  /** �I���t���O */
  private boolean fDoneFlag;

  /** �l�b�g���[�N��UMart�N���X�ւ̎Q�� */
  UMartNetwork fUMartNetwork;

  /**
   * �R���X�g���N�^
   * @param umart UMartNetwork�I�u�W�F�N�g
   */
  public ULoginManagerNetwork(UMartNetwork umart) {
    super();
    fUMartNetwork = umart;
    fThread = null;
    fServerSocket = null;

  }

  /**
     ���[�U�[����̐ڑ��v�����Ď����郁�C�����[�v�B
   */
  public void run() {
    fDoneFlag = false;
    try {
      while (!fDoneFlag) {
        Socket s = fServerSocket.accept();
        new UAgentForNetworkClient(s, fUMartNetwork);
      }
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
    fThread = null;
  }

  /**
   * �l�b�g���[�N�E�G�[�W�F���g����̐ڑ���t���J�n����D
   * @param port �ڑ���t�|�[�g
   */
  public void startLoop(int port) {
    try {
      fServerSocket = new ServerSocket(port);
    } catch (Exception e) {
      System.err.println("Error: " + e);
      System.exit(5);
    }
    fThread = new Thread(this);
    fThread.start();
  }

  /**
     ���C�����[�v���I������B
   */
  public void stopLoop() {
    fDoneFlag = false;
  }

}
