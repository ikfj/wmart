package org.wmart.cmdCore;

import java.util.*;

/**
 * SVMP�R�}���h�I�u�W�F�N�g�W����ێ�����v���g�R���N���X�ł��D
 * SVMP�R�}���h�����L�[�Ƃ��Ďg���ēK�؂�SVMP�I�u�W�F�N�g���擾���邱�Ƃ��ł��܂��D
 * @author ����@��
 */
public abstract class WProtocolCore {

	/** SVMP�R�}���h�I�u�W�F�N�g�̃f�[�^�x�[�X */
  protected HashMap fCommandHash;

  /**
   *�R���X�g���N�^
   */
  public WProtocolCore() {
    fCommandHash = new HashMap();
  }

  /**
   * SVMP�R�}���h�I�u�W�F�N�g�̃f�[�^�x�[�X��Ԃ��D
   * @return SVMP�R�}���h�I�u�W�F�N�g�̃f�[�^�x�[�X(HashMap)
   */
  public HashMap getCommandHashMap() {
    return fCommandHash;
  }

  /**
   * �R�}���h����cmdStr��SVMP�R�}���h�I�u�W�F�N�g��Ԃ��D
   * @param cmdStr �R�}���h��
   * @return SVMP�R�}���h�I�u�W�F�N�g
   */
  public ICommand getCommand(String cmdStr) {
    return (ICommand)fCommandHash.get(cmdStr);
  }
}
