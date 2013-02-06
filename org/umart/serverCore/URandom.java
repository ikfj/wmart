package org.umart.serverCore;

import java.util.*;

/**
 * 乱数を扱うクラスです．
 * @author 小野　功
 */
public class URandom {

  /** 乱数の種 */
  private static long fSeed;

  /** 乱数生成器 */
  private static Random fRandom = null;

  /**
   * 乱数の種をシステム時刻で初期化した乱数オブジェクトを返す．
   * @return 乱数オブジェクト
   */
  public static Random getInstance() {
    if (fRandom == null) {
      fSeed = System.currentTimeMillis();
      fRandom = new Random(fSeed);
    }
    return fRandom;
  }

  /**
   * 乱数の種を設定する．
   * @param seed 乱数の種
   */
  public static void setSeed(long seed) {
    fSeed = seed;
    fRandom = new Random(fSeed);
  }

  /**
   * 乱数の種を返す．
   * @return 乱数の種
   */
  public static long getSeed() {
    return fSeed;
  }

}
