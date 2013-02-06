package org.umart.serverSA;

import org.umart.serverCore.ULoginManager;
import org.umart.serverCore.UMart;
import org.umart.serverCore.UMemberList;
import org.umart.serverCore.UPriceInfoDB;

/**
 * �X�^���h�A�������p�̎�����N���X�ł��D
 * @author ����@��
 */
public class UMartStandAlone extends UMart {

  /**
   * UMartStandAlone�I�u�W�F�N�g�̐�������я��������s���F<br>
   * - �G�[�W�F���g�͒N�����O�C�����Ă��Ȃ����<br>
   * - ���t��1�C�񂹉񐔂�1<br>
   * - ��Ԃ�BEFORE_TRADING<br>
   * @param noOfMembers �����
   * @param priceInfoDB ���i�n��f�[�^�x�[�X
   * @param startPoint ���i�n��̉��Ԗڂ̃f�[�^�������̃f�[�^�Ƃ��邩
   * @param seed �����̎�
   * @param maxDate �T�[�o�[�^�p����
   * @param noOfSessionsPerDay ���������̐ߐ�
   * @param initialCash ����������
   * @param tradingUnit ����P��
   * @param marginRate �؋�����
   * @param feePerUnit �P�ʓ�����̎���萔��
   * @param maxLoan �؂������x�z
   * @param interest �؂�������
   */
  public UMartStandAlone(UMemberList members, UPriceInfoDB priceInfoDB,
                         int startPoint, long seed,
                         int maxDate, int noOfSessionsPerDay) {
    super(members, priceInfoDB, startPoint, seed,
          maxDate, noOfSessionsPerDay);
  }

  /**
   * @see org.umart.serverCore.UMart#createLoginManager()
   */
  protected ULoginManager createLoginManager() {
    return new ULoginManager();
  }

}
