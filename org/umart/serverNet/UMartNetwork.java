package org.umart.serverNet;

import org.umart.serverCore.*;

/**
 * �l�b�g���[�N���œ��삷�������ł��D
 * @author ����@��
 */
public class UMartNetwork extends UMart {

  /** �T�[�o�[�\�P�b�g�̃|�[�g�ԍ��̃f�t�H���g�l */
  public static final int DEFAULT_PORT = 5010;

  /** �T�[�o�[�\�P�b�g�̃|�[�g�ԍ� */
  private int fPort = DEFAULT_PORT;

  /**
   * �f�t�H���g�|�[�g��UMartNetwork�I�u�W�F�N�g�̐�������я��������s���F<br>
   * - �G�[�W�F���g�͒N�����O�C�����Ă��Ȃ����<br>
   * - ���t��1�C�߂�1<br>
   * - ��Ԃ�BEFORE_TRADING<br>
   * @param members ������X�g
   * @param priceInfoDB ���i�n��f�[�^�x�[�X
   * @param startPoint ���i�n��̊J�n�|�C���g
   * @param seed �����̎�
   * @param maxDate �������
   * @param noOfSessionsPerDay 1��������̐ߐ�
   */
  public UMartNetwork(UMemberList members, UPriceInfoDB priceInfoDB,
                      int startPoint, long seed,
                      int maxDate, int noOfSessionsPerDay) {
    this(members, priceInfoDB, startPoint, seed, maxDate, noOfSessionsPerDay, DEFAULT_PORT);
  }

  /**
   * UMartNetwork�I�u�W�F�N�g�̐�������я��������s���F<br>
   * - �G�[�W�F���g�͒N�����O�C�����Ă��Ȃ����<br>
   * - ���t��1�C�߂�1<br>
   * - ��Ԃ�BEFORE_TRADING<br>
   * @param members ������X�g
   * @param priceInfoDB ���i�n��f�[�^�x�[�X
   * @param startPoint ���i�n��̊J�n�|�C���g
   * @param seed �����̎�
   * @param maxDate �������
   * @param noOfSessionsPerDay 1��������̐ߐ�
   * @param port �T�[�o�[�\�P�b�g�̃|�[�g�ԍ�
   */
  public UMartNetwork(UMemberList members, UPriceInfoDB priceInfoDB,
                      int startPoint, long seed,
                      int maxDate, int noOfSessionsPerDay,
                      int port) {
    super(members, priceInfoDB, startPoint, seed, maxDate, noOfSessionsPerDay);
    fPort = port;
  }

  /**
   * @see org.umart.serverCore.UMart#createLoginManager()
   */
  protected ULoginManager createLoginManager() {
    return new ULoginManagerNetwork(this);
  }
  
  /**
   * ���O�C���}�l�[�W�����N������D
   */
  public void startLoginManager() {
    ((ULoginManagerNetwork)fLoginManager).startLoop(fPort);
  }

}
