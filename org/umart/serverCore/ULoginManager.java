package org.umart.serverCore;

import java.util.*;

/**
 * �S�����o�[�̃��O�C�����Ǘ����܂��D
 * @author �암 �T�i
 * @author �X�� �̕�
 * @author ���� ��
 */

public class ULoginManager {

  /** �S�����o�[�̃��O�C����Ԃ��i�[���邽�߂̃x�N�^ */
  private Vector fLoginStatusArray;

  /**
   * ULoginStatusManager�I�u�W�F�N�g�̐�������я��������s���B
   */
  public ULoginManager() {
    fLoginStatusArray = new Vector();
  }

  /**
   * �����ŗ^�����郆�[�U�[���ɂ��������ĐV�������O�C����Ԃ�
   * ����, �o�^���s���B������, ���ꃆ�[�U�[���̐ڑ���Ԃ���邱�Ƃ�
   * �ł��Ȃ��B
   * @param id ���[�U�[ID
   * @return true: ����, false: ���s
   */
  public boolean creatLoginStatus(int id) {
    Enumeration e = fLoginStatusArray.elements();
    while (e.hasMoreElements()) {
      ULoginStatus x = (ULoginStatus) e.nextElement();
      if (x.getUserID() == id) {
        System.out.print("ERROR:ULoginStatusManager::createLoginStatus: " + id);
        System.err.println(" has already existed.");
        return false;
      }
    }
    fLoginStatusArray.addElement(new ULoginStatus(id));
    return true;
  }

  /**
   * �����ŗ^�����郆�[�U�[ID�̃��O�C����Ԃ�Ԃ��B
   * ������Ȃ��ꍇ��, null��Ԃ��B
   * @param userID ���[�U�[ID
   * @return �Ή����郍�O�C����ԃI�u�W�F�N�g�B������, �Ή����鎖������
   *         ������Ȃ��ꍇ��null�B
   */
  public ULoginStatus findLoginStatus(int userID) {
    Enumeration e = fLoginStatusArray.elements();
    while (e.hasMoreElements()) {
      ULoginStatus x = (ULoginStatus) e.nextElement();
      if (x.getUserID() == userID) {
        return x;
      }
    }
    return null;
  }

  /**
   * �o�^����Ă���S�Ẵ��O�C����Ԃ��܂ރx�N�^��Ԃ��B
   * @return ���O�C����Ԃ��܂ރx�N�^
   */
  public Vector getLoginStatuses() {
    return fLoginStatusArray;
  }

}
