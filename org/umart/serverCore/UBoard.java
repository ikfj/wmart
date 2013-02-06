package org.umart.serverCore;

import java.util.*;

/**
 * �񂹂��s���N���X�ł��D�g�p�菇�͈ȉ��̂Ƃ���ł��F
 * <ul>
 * <li>�񂹂̑ΏۂƂ������S�Ă̒�����UBoard.appendOrder���\�b�h�ɂ��
 *     �o�^����B
 * <li>UBoard.makeContracts���\�b�h���Ăяo���Ĕ񂹂��s���B
 * <li>UBoard.getOrderArray���\�b�h���Ăяo���Ē���(UOrder)����肾���B
 * </ul>
 * @author �암 �S�i
 * @author �X�� �̕�
 * @author ���� ��
 */
public class UBoard {

  /** ����(UOrder)���i�[���邽�߂̃c���[�Z�b�g. 2���؂𗘗p���Ă���. */
  private TreeSet fOrderTreeSet;

  /** ��肵�����蒍���̂����ł������������i�B���i����ɗp������B */
  private long fMaxSellPrice;

  /** ��肵�Ȃ��������蒍���̂����ł��Ⴂ�������i�B���i����ɗp������B */
  private long fMinNotSellPrice;

  /** ��肵�����������̂����ł��Ⴂ�������i�B���i����ɗp������B */
  private long fMinBuyPrice;

  /** ��肵�Ȃ��������������̂����ł������������i�B���i����ɗp������B */
  private long fMaxNotBuyPrice;

  /** ��萔�� */
  private long fContractVolume;

  /** ��艿�i */
  private long fContractPrice;

  /** �̍ŏI�X�V���� */
  private Date fLastUpdateTime;

  /** ���ID�����̂��߂̃J�E���^ */
  private static long fContractID = 1;

  /**
   * ���̖��ID��Ԃ��B
   * @return ���ID
   */
  private static long getNextContractID() {
    long result = fContractID;
    ++fContractID;
    return result;
  }

  /**
   * �񂹃N���X�𐶐�, ����������B
   */
  public UBoard() {
    fOrderTreeSet = new TreeSet(new UOrderComparator());
    fMaxSellPrice = 0;
    fMinNotSellPrice = 0;
    fMinBuyPrice = 0;
    fMaxNotBuyPrice = 0;
    fContractVolume = 0;
    fLastUpdateTime = new Date();
  }

  /**
   * ����(UOrder)���i�[���������q��Ԃ��B
   * @return ����(UOrder)���i�[���������q
   */
  public final Iterator getOrderArray() {
    return fOrderTreeSet.iterator();
  }

  /**
   * ����info�ɐݒ肵�ĕԂ��D
   * @param info ���
   */
  public final void setBoardInfo(UBoardInformation info) {
    synchronized (fOrderTreeSet) {
      info.setLastUpdateTime(this.getLastUpdateTime());
      Iterator orders = fOrderTreeSet.iterator();
      while (orders.hasNext()) {
        UOrder o = (UOrder) orders.next();
        info.addInformation(o.getSellBuy(), o.getMarketLimit(),
                            o.getPrice(), o.getVolume());
      }
    }
  }

  /**
   * �̍ŏI�X�V������Ԃ��D
   * @return �̍ŏI�X�V����
   */
  public Date getLastUpdateTime() {
    return fLastUpdateTime;
  }

  /**
   * ��萔�ʂ�Ԃ��D
   * @return ��萔��
   */
  public long getContractVolume() {
    return fContractVolume;
  }

  /**
   * ��艿�i��Ԃ��D
   * @return ��艿�i
   */
  public long getContractPrice() {
    return fContractPrice;
  }

  /**
   * ����C�z�l(���蒍�����i�̍ň��l)��Ԃ��B������肪�����0��Ԃ��B
   * �܂�, �S�Ă̔��蒍�������s���Ȃ��-1��Ԃ��B
   * @return ���蒍�����i�̍ň��l�B������, ������肪�����0�B
   *         �S�Ă̔��蒍�������s���Ȃ��-1�B
   */
  public long getAskedQuotation() {
    if (fContractVolume > 0) {
      return 0;
    }
    Iterator itr = fOrderTreeSet.iterator();
    while (itr.hasNext()) {
      UOrder o = (UOrder) itr.next();
      if (o.getSellBuy() == UOrder.SELL && o.getMarketLimit() == UOrder.LIMIT) {
        return o.getPrice();
      }
    }
    return -1;
  }

  /**
   * �����C�z�l(�����������i�̍ō��l)��Ԃ��B������肪�����0��Ԃ��B
   * �܂�, �S�Ă̔������������s���Ȃ��-1��Ԃ��B
   * @return �����������i�̍ň��l�B������, ������肪�����0�B
   *         �S�Ă̔������������s���Ȃ��-1�B
   */
  public long getBidQuotation() {
    if (fContractVolume > 0) {
      return 0;
    }
    Iterator itr = fOrderTreeSet.iterator();
    while (itr.hasNext()) {
      UOrder o = (UOrder) itr.next();
      if (o.getSellBuy() == UOrder.BUY && o.getMarketLimit() == UOrder.LIMIT) {
        return o.getPrice();
      }
    }
    return -1;
  }

  /**
   * ����(UOrder�I�u�W�F�N�g)��o�^����B
   * �V���Ȓ�����UOrderComparator�N���X�ɏ]����2���؂ɑ}�������.<br>
   * @param order ����
   */
  public final void appendOrder(UOrder order) {
    synchronized (fOrderTreeSet) {
      if (!fOrderTreeSet.add(order)) {
        System.err.println("Error in UBoard.appendOrder.");
        System.exit(1);
      }
      fLastUpdateTime = new Date();
    }
  }

  /**
   * �w�肳�ꂽ�������폜����D
   * @param userID ���[�U�[ID
   * @param orderID ����ID
   * @return true:�����Cfalse:���s
   */
  public boolean removeOrder(int userID, long orderID) {
    synchronized (fOrderTreeSet) {
      Iterator itr = fOrderTreeSet.iterator();
      while (itr.hasNext()) {
        UOrder o = (UOrder) itr.next();
        if (o.getUserID() == userID && o.getOrderID() == orderID) {
          itr.remove();
          fLastUpdateTime = new Date();
          return true;
        }
      }
      return false;
    }
  }

  /**
   * �o�^����Ă���S�Ă̒������폜����B
   */
  public final void clear() {
    fOrderTreeSet.clear();
    fLastUpdateTime = new Date();
  }

  /**
   * �񂹂��s���B
   * @param date ��
   * @param session ��
   * @return true: ��肠��, false: ���Ȃ�
   */
  public boolean makeContracts(int date, int session) {
    fContractPrice = UPriceInfo.INVALID_PRICE;
    if (fOrderTreeSet.size() < 2) { // ��������2��菬����
      return false;
    }
    long maxPrice = searchMaxPrice(fOrderTreeSet);
    if (maxPrice == 0) { // �S�Đ��s����
      return false;
    }
    executeContract(maxPrice, date, session);
    if (fContractVolume == 0) {
      return false;
    }
    fContractPrice = determineContractPrice(maxPrice);
    setContractPrice(fContractPrice);
    return true;
  }

  /**
   * �񂹂̌��ʂ����艿�i�����肷��B
   * @param maxPrice �o�^����Ă��钍���ɂ�����ō��������i
   * @return ��艿�i
   */
  private long determineContractPrice(long maxPrice) {
    if (Math.max(fMaxSellPrice, fMaxNotBuyPrice) == 0) {
      return Math.min(fMinBuyPrice, fMinNotSellPrice);
    } else if (Math.min(fMinBuyPrice, fMinNotSellPrice) == maxPrice + 1) {
      return Math.max(fMaxSellPrice, fMaxNotBuyPrice);
    } else {
      return (Math.max(fMaxSellPrice, fMaxNotBuyPrice)
              + Math.min(fMinBuyPrice, fMinNotSellPrice) + 1) / 2;
    }
  }

  /**
   * ��葀����s���B<br>
   * @param maxPrice �o�^����Ă��钍���ɂ�����ō��������i
   * @param date ��
   * @param session ��
   */
  private void executeContract(long maxPrice, int date, int session) {
    long prevBuyVolume = calcTotalBuy(fOrderTreeSet);
    long prevSellVolume = 0;
    long currentBuyVolume = prevBuyVolume;
    long currentSellVolume = 0;
    Date time = new Date();
    fMaxSellPrice = 0;
    fMinNotSellPrice = maxPrice + 1;
    fMinBuyPrice = maxPrice + 1;
    fMaxNotBuyPrice = 0;
    fContractVolume = 0;
    ArrayList marketBuyOrders = new ArrayList();
    Iterator itr = fOrderTreeSet.iterator();
    while (itr.hasNext()) {
      UOrder o = (UOrder) itr.next();
      if (o.getMarketLimit() == UOrder.MARKET && o.getSellBuy() == UOrder.BUY) {
        o.setPrice(maxPrice + 1);
        marketBuyOrders.add(o);
      }
      long volume = o.getUncontractOrderVolume();
      if (o.getSellBuy() == UOrder.BUY) {
        currentBuyVolume -= volume;
      } else if (o.getSellBuy() == UOrder.SELL) {
        currentSellVolume += volume;
      } else {
        System.out.println("Error in UBoard.executeContract");
        System.out.println("o.getSellBuy=" + o.getSellBuy());
        System.exit(5);
      }
      if (prevBuyVolume > prevSellVolume &&
          currentBuyVolume >= currentSellVolume) {
        doCase1(currentBuyVolume, currentSellVolume, o, time, date, session);
      } else if (prevBuyVolume > prevSellVolume &&
                 currentBuyVolume < currentSellVolume) {
        doCase2(prevBuyVolume, currentBuyVolume, prevSellVolume,
                currentSellVolume, o, time, date, session);
      } else if (prevBuyVolume <= prevSellVolume &&
                 currentBuyVolume < currentSellVolume) {
        doCase3(currentBuyVolume, currentSellVolume, o, time, date, session);
      } else {
        System.out.println("Error in UBoard.executeContract");
        System.out.println("prevBuyVolume=" + prevBuyVolume);
        System.out.println("prevSellVolume=" + prevSellVolume);
        System.out.println("currentBuyVolume=" + currentBuyVolume);
        System.out.println("currentSellVolume=" + currentSellVolume);
        System.exit(5);
      }
      prevBuyVolume = currentBuyVolume;
      prevSellVolume = currentSellVolume;
    }
    Iterator itr2 = marketBuyOrders.iterator();
    while (itr2.hasNext()) {
      UOrder o = (UOrder) itr2.next();
      if (o.getMarketLimit() != UOrder.MARKET) {
        System.err.println(
            "o.getMarketLimit() != o.MARKET in UBoard.executeContract");
        System.exit(5);
      }
      o.setPrice(0);
    }
  }

  /**
   * ���v�Ȑ�(��)�������Ȑ�(��)��荂���ꍇ�̒����ɂ��Ė�葀����s���B<br>
   * @param buyVolume ���݂̔�����������
   * @param sellVolume ���݂̔��蒍������
   * @param o �ΏۂƂȂ钍��
   * @param time ������
   * @param date ��
   * @param session ��
   */
  private void doCase1(long buyVolume, long sellVolume, UOrder o,
                       Date time, int date, int session) {
    if (o.getSellBuy() == UOrder.BUY) { // ���������Ȃ�Εs���
      if (fMaxNotBuyPrice < o.getPrice() && o.getMarketLimit() == UOrder.LIMIT) {
        fMaxNotBuyPrice = o.getPrice();
      }
    } else { // ���蒍���Ȃ�Ζ��
      long contractID = getNextContractID();
      o.addContractInformation(contractID, time, o.getUncontractOrderVolume(),
                               date, session);
      if (fMaxSellPrice < o.getPrice() && o.getMarketLimit() == UOrder.LIMIT) {
        fMaxSellPrice = o.getPrice();
      }
      if (fContractVolume < sellVolume) {
        fContractVolume = sellVolume;
      }
    }
  }

  /**
   * ���v�Ȑ�(��)�Ƌ����Ȑ�(��)����������ꍇ�̒����ɂ���
   * ��葀����s���B
   * @param prevBuyVolume ��X�e�b�v�O�̔�����������
   * @param currentBuyVolume ���݂̔�����������
   * @param prevSellVolume ��X�e�b�v�O�̔��蒍������
   * @param currentSellVolume ���݂̔��蒍������
   * @param o �ΏۂƂȂ钍��
   * @param time ������
   * @param date ��
   * @param session ��
   */
  private void doCase2(long prevBuyVolume, long currentBuyVolume,
                       long prevSellVolume, long currentSellVolume,
                       UOrder o, Date time, int date, int session) {
    if (o.getSellBuy() == UOrder.BUY) { // ���v�Ȑ�(��)�������Ȑ�(��)��
      // �ォ�牺�֓˂��������ꍇ
      long volume = currentSellVolume - currentBuyVolume;
      long contractID = getNextContractID();
      o.addContractInformation(contractID, time, volume, date, session);
      if (fMinBuyPrice > o.getPrice() && o.getMarketLimit() == UOrder.LIMIT) {
        fMinBuyPrice = o.getPrice();
      }
      if (fContractVolume < currentSellVolume) {
        fContractVolume = currentSellVolume;
      }
    } else { // �����Ȑ�(��)�����v�Ȑ�(��)���������ɓ˂��������ꍇ
      long volume = currentBuyVolume - prevSellVolume;
      long contractID = getNextContractID();
      o.addContractInformation(contractID, time, volume, date, session);
      if (fMaxSellPrice < o.getPrice() && o.getMarketLimit() == UOrder.LIMIT) {
        fMaxSellPrice = o.getPrice();
      }
      if (fContractVolume < currentBuyVolume) {
        fContractVolume = currentBuyVolume;
      }
    }
  }

  /**
   * ���v�Ȑ�(��)�������Ȑ�(��)���Ⴂ�ꍇ�̒����ɂ���
   * ��葀����s���B
   * @param buyVolume ���݂̔�����������
   * @param sellVolume ���݂̔��蒍������
   * @param o �ΏۂƂȂ钍��
   * @param time ������
   * @param date ��
   * @param session ��
   */
  private void doCase3(long buyVolume, long sellVolume,
                       UOrder o, Date time, int date, int session) {
    if (o.getSellBuy() == UOrder.BUY) { // ��������
      long contractID = getNextContractID();
      long volume = o.getUncontractOrderVolume();
      o.addContractInformation(contractID, time, volume, date, session);
      if (fMinBuyPrice > o.getPrice() && o.getMarketLimit() == UOrder.LIMIT) {
        fMinBuyPrice = o.getPrice();
      }
    } else { // ���蒍��
      if (fMinNotSellPrice > o.getPrice() && o.getMarketLimit() == UOrder.LIMIT) {
        fMinNotSellPrice = o.getPrice();
      }
    }
  }

  /**
   * �o�^����Ă��钍���ɖ�艿�i��ݒ肷��B
   * @param contractPrice ��艿�i
   */
  private void setContractPrice(long contractPrice) {
    Iterator itr = fOrderTreeSet.iterator();
    while (itr.hasNext()) {
      UOrder o = (UOrder) itr.next();
      o.setLatestContractPrice(contractPrice);
    }
  }

  /**
   * �����ŗ^����ꂽ�x�N�^�ɓo�^����Ă��钍���̒���
   * �ł������������i��Ԃ��B
   * @param orderArray �����̓o�^���ꂽ�x�N�^
   * @return �ō��������i
   */
  private static long searchMaxPrice(TreeSet orderTreeSet) {
    long maxprice = 0;
    Iterator itr = orderTreeSet.iterator();
    while (itr.hasNext()) {
      UOrder u = (UOrder) itr.next();
      if (u.getPrice() > maxprice) {
        maxprice = u.getPrice();
      }
    }
    return maxprice;
  }

  /**
   * �����ŗ^����ꂽ�x�N�^�ɓo�^����Ă��锃�������̐��ʂ̍��v��Ԃ��B<br>
   * @param orderArray �����̓o�^���ꂽ�x�N�^
   * @return ���������̐��ʂ̍��v
   */
  private static long calcTotalBuy(TreeSet orderTreeSet) {
    long totalBuy = 0;
    Iterator itr = orderTreeSet.iterator();
    while (itr.hasNext()) {
      UOrder u = (UOrder) itr.next();
      if (u.getSellBuy() == UOrder.BUY) {
        totalBuy += u.getUncontractOrderVolume();

      }
    }
    return totalBuy;
  }

  /**
   * �e�X�g�p���C���֐�
   */
  public static void main(String args[]) {
    UBoard board = new UBoard();
    UOrderManager orderManager = new UOrderManager();
    UOrder o1 =
        orderManager.createOrder(1, "User1", "j30", UOrder.SELL, UOrder.LIMIT,
                                 UOrder.NEW, 3000, 10, 1, 1);
    board.appendOrder(o1);
    UOrder o2 =
        orderManager.createOrder(2, "User2,", "j30", UOrder.BUY, UOrder.MARKET,
                                 UOrder.NEW, 3100, 10, 1, 1);
    board.appendOrder(o2);
    UOrder o3 =
        orderManager.createOrder(3, "User3", "j30", UOrder.BUY, UOrder.LIMIT,
                                 UOrder.NEW, 2800, 10, 1, 1);
    board.appendOrder(o3);
    UOrder o4 =
        orderManager.createOrder(4, "User4", "j30", UOrder.SELL, UOrder.MARKET,
                                 UOrder.NEW, 2700, 10, 1, 1);
    board.appendOrder(o4);
    UOrder o5 =
        orderManager.createOrder(5, "User5", "j30", UOrder.SELL, UOrder.LIMIT,
                                 UOrder.NEW, 3300, 10, 1, 1);
    board.appendOrder(o5);
    UOrder o6 =
        orderManager.createOrder(6, "User6", "j30", UOrder.BUY, UOrder.MARKET,
                                 UOrder.NEW, 3200, 10, 1, 1);
    board.appendOrder(o6);
    Iterator itr1 = board.getOrderArray();
    while (itr1.hasNext()) {
      UOrder o = (UOrder) itr1.next();
      System.out.println(o.getUserID());
    }
    System.out.println("*******************");
    if (!board.removeOrder(o1.getUserID(), o1.getOrderID())) {
      System.err.println("Can't remove o1");
      System.exit(5);
    }
    Iterator itr2 = board.getOrderArray();
    while (itr2.hasNext()) {
      UOrder o = (UOrder) itr2.next();
      System.out.println(o.getUserID());
    }
  }

}
