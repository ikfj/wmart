package org.umart.serverCore;

import java.util.*;

/**
 * �����������N���X�ł��D
 * @author ����@��
 */
public class URandom {

  /** �����̎� */
  private static long fSeed;

  /** ���������� */
  private static Random fRandom = null;

  /**
   * �����̎���V�X�e�������ŏ��������������I�u�W�F�N�g��Ԃ��D
   * @return �����I�u�W�F�N�g
   */
  public static Random getInstance() {
    if (fRandom == null) {
      fSeed = System.currentTimeMillis();
      fRandom = new Random(fSeed);
    }
    return fRandom;
  }

  /**
   * �����̎��ݒ肷��D
   * @param seed �����̎�
   */
  public static void setSeed(long seed) {
    fSeed = seed;
    fRandom = new Random(fSeed);
  }

  /**
   * �����̎��Ԃ��D
   * @return �����̎�
   */
  public static long getSeed() {
    return fSeed;
  }

}
