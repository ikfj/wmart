package org.wmart.cmdClientSA;

import org.wmart.core.*;

/**
 * ���[�J���œ��삷��N���C�A���g�̂��߂�SVMP�R�}���h�̒�`�ɕK�v�ȃC���^�[�t�F�[�X�ł��D
 * @author ����@��
 */

public interface IClientCmdSA {

  /**
   * �T�[�o�[�ւ̎Q�Ƃƃ��[�U�[ID��ݒ肷��D
   * @param umart �T�[�o�[�ւ̎Q��
   * @param userID ���[�U�[ID
   */
  public void setConnection(WMart wmart, int userID);

}
