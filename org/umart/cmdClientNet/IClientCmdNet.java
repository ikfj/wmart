package org.umart.cmdClientNet;

import java.io.*;

/**
 * IServerCmd�C���^�[�t�F�[�X�́CNetwork�ł̃N���C�A���g��Ŏ��s�����R�}���h�ɕK�v�ȃC���^�[�t�F�[�X���`����D
 *
 * @author ����@��
 */

public interface IClientCmdNet {

  /**
   * �T�[�o�[�Ƃ̒ʐM�ɕK�v�ȓ��o�͂̃X�g���[����ݒ肷��D
   * @param br ����
   * @param pw �o��
   */
  public void setConnection(BufferedReader br, PrintWriter pw);

}
