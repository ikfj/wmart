package org.umart.cmdServer;

import org.umart.serverNet.*;

/**
 * �T�[�o��œ��삷��SVMP�R�}���h�N���X�ɕK�v�ȃC���^�[�t�F�[�X���`���Ă��܂��D
 * @author ����@��
 */

public interface IServerCmd {

  /**
   * ���̃R�}���h�����s�����G�[�W�F���g�ƃT�[�o�[��ݒ肷��D
   * @param agent ���̃R�}���h�����s�����G�[�W�F���g
   * @param umart �T�[�o�[�ւ̎Q��
   */
  public void setConnection(UAgentForNetworkClient agent, UMartNetwork umart);

}
