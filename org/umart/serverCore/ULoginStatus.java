package org.umart.serverCore;

/**
 * �e���[�U�[(�����o�[)�ɑ΂���ڑ����̃N���C�A���g�����Ǘ����܂��D
 * U-Mart�V�X�e���ł́C���ꃆ�[�U���������O�C�����邱�Ƃ������Ă��邽�߁C
 * �ڑ����̃N���C�A���g����ێ����Ă��܂��D
 * @author �암 �T�i
 * @author �X�� �̕�
 * @author ���� ��
 */

public class ULoginStatus {

  /** �ڑ����̃N���C�A���g�� */
  private int fNoOfLoginAgents;

  /** ���[�U�[ID */
  private int fUserID;

  /**
   * ULoginStatus�I�u�W�F�N�g�𐶐�, ����������B
   * @param userID ���[�U�[ID
   * @param umart U-Mart�ւ̎Q��
   */
  public ULoginStatus(int userID) {
    fUserID = userID;
    fNoOfLoginAgents = 0;
  }

  /**
   * ���[�U�[ID��Ԃ��B
   * @return ���[�U�[ID
   */
  public int getUserID() {
    return fUserID;
  }

  /**
   * ���O�C�����̃��[�U�[����1���������B
   */
  synchronized public void incrementNoOfLoginAgents() {
    fNoOfLoginAgents++;
    // System.out.println("No of userID" + fUserID + " is " + fNoOfLoginAgents);
  }

  /**
   * ���O�C�����̃��[�U�[����1�������炷�B
   */
  synchronized public void decrementNoOfLoginAgents() {
    if (fNoOfLoginAgents-- < 0) {
      System.out.println("Agent number is minus");
      System.exit(1);
    }
    System.out.println("No of userID" + fUserID + " is " + fNoOfLoginAgents);
  }

  /**
   * ���O�C�����̃��[�U�[����Ԃ��D
   * @return ���O�C�����̃��[�U�[��
   */
  synchronized public int getNoOfLoginAgents() {
    return fNoOfLoginAgents;
  }

}
