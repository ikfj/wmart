package org.umart.cmdCore;

import java.util.*;

public abstract class UCMemberProfileCore implements ICommand {

  /** �R�}���h�� */
  public static final String CMD_NAME = "MemberProfile";

  /** �R�}���h�ւ̈����F�Ώۉ����ID(-1�̏ꍇ�C�������g) */
  protected int fTargetUserId;

  /** �R�}���h�̎��s���ʂ̏�� */
  protected UCommandStatus fCommandStatus;

  /** ������ */
  protected HashMap fData;

  /** ���O�C�������������߂̃L�[ */
  public static final String STRING_LOGIN_NAME = "STRING_LOGIN_NAME";

  /** �p�X���[�h���������߂̃L�[ */
  public static final String STRING_PASSWORD = "STRING_PASSWORD";

  /** �G�[�W�F���g����(Human or Machine)���������߂̃L�[ */
  public static final String STRING_ATTRIBUTE = "STRING_ATTRIBUTE";

  /** �R�l�N�V����(Remote or Local)���������߂̃L�[ */
  public static final String STRING_CONNECTION = "STRING_CONNECTION";

  /** �A�N�Z�X�������������߂̃L�[ */
  public static final String ARRAY_LIST_ACCESS = "ARRAY_LIST_ACCESS";

  /** ���ۂ̖��O���������߂̃L�[ */
  public static final String STRING_REAL_NAME = "STRING_REAL_NAME";

  /** �V�X�e���p�����[�^���i�[����ArrayList���������߂̃L�[ */
  public static final String ARRAY_LIST_SYSTEM_PARAMETERS =
      "ARRAY_LIST_SYSTEM_PARAMETERS";

  /** �����̎���������߂̃L�[ */
  public static final String INT_SEED = "INT_SEED";

  /** �������Y���������߂̃L�[ */
  public static final String LONG_INITIAL_CASH = "LONG_INITIAL_CASH";

  /** ����P�ʂ��������߂̃L�[ */
  public static final String LONG_TRADING_UNIT = "LONG_TRADING_UNIT";

  /** �P�ʎ��������̎萔�����������߂̃L�[ */
  public static final String LONG_FEE_PER_UNIT = "LONG_FEE_PER_UNIT";

  /** �؋��������������߂̃L�[ */
  public static final String LONG_MARGIN_RATE = "LONG_MARGIN_RATE";

  /** �؂������x�z���������߂̃L�[ */
  public static final String LONG_MAX_LOAN = "LONG_MAX_LOAN";

  /** �؂����������������߂̃L�[ */
  public static final String DOUBLE_INTEREST = "DOUBLE_INTEREST";

  /** ����\(+1),����s�\(-1)���������߂̃L�[ */
  public static final String INT_STATUS = "INT_STATUS";

  /** ���O�C�����Ă���G�[�W�F���g�̐� */
  public static final String INT_NO_OF_LOGIN_AGENTS = "INT_NO_OF_LOGIN_AGENTS";

  /** �O�����x���������߂̃L�[ */
  public static final String HASHMAP_YESTERDAY_BALANCE =
      "HASHMAP_YESTERDAY_BALANCE";

  /** �������x���������߂̃L�[ */
  public static final String HASHMAP_TODAY_BALANCE = "HASHMAP_TODAY_BALANCE";

  /** �ؓ������������߂̃L�[ */
  public static final String LONG_LOAN = "LONG_LOAN";

  /** ���������v���������߂̃L�[ */
  public static final String LONG_UNREALIZED_PROFIT = "LONG_UNREALIZED_PROFIT";

  /** �a���؋������������߂̃L�[ */
  public static final String LONG_MARGIN = "LONG_MARGIN";

  /** ���x�����萔�����������߂̃L�[ */
  public static final String LONG_SUM_OF_FEE = "LONG_SUM_OF_FEE";

  /** ���x�����������������߂̃L�[ */
  public static final String LONG_SUM_OF_INTEREST = "LONG_SUM_OF_INTEREST";

  /** �ۗL�������������߂̃L�[ */
  public static final String LONG_CASH = "LONG_CASH";

  /** �������v���������߂̃L�[ */
  public static final String LONG_PROFIT = "LONG_PROFIT";

  /** �|�W�V�������������߂̃L�[ */
  public static final String HASHMAP_POSITION = "HASHMAP_POSITION";

  /** �O���܂ł̔��|�W�V�����̍��v���������߂̃L�[ */
  public static final String LONG_SUM_OF_SELL_POSITIONS_UNTIL_YESTERDAY =
      "LONG_SUM_OF_SELL_POSITIONS_UNTIL_YESTERDAY";

  /** �O���܂ł̔��|�W�V�����̍��v���������߂̃L�[ */
  public static final String LONG_SUM_OF_BUY_POSITIONS_UNTIL_YESTERDAY =
      "LONG_SUM_OF_BUY_POSITIONS_UNTIL_YESTERDAY";

  /** �����̔��|�W�V�����̍��v���������߂̃L�[ */
  public static final String LONG_TODAY_SELL_POSITIONS =
      "LONG_TODAY_SELL_POSITIONS";

  /** �����̔��|�W�V�����̍��v���������߂̃L�[ */
  public static final String LONG_TODAY_BUY_POSITIONS =
      "LONG_TODAY_BUY_POSITIONS";

  /**�@�R���X�g���N�^ */
  public UCMemberProfileCore() {
    super();
    fCommandStatus = new UCommandStatus();
    fData = new HashMap();
  }

  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME)) {
      return true;
    } else {
      return false;
    }
  }

  public String getName() {
    return CMD_NAME;
  }

  public boolean setArguments(StringTokenizer st) {
    try {
      fTargetUserId = Integer.parseInt(st.nextToken());
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public void setArguments(int targetUserId) {
    fTargetUserId = targetUserId;
  }

  public UCommandStatus getStatus() {
    return fCommandStatus;
  }

  public void printOn() {
    System.out.println(fData.toString());
  }

  public HashMap getData() {
    return fData;
  }

  public String getResultString() {
    String returnStr = "";
    returnStr += fData.get(UCMemberProfileCore.STRING_LOGIN_NAME).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.STRING_PASSWORD).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.STRING_ATTRIBUTE).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.STRING_CONNECTION).toString() +
        "\n";
    returnStr +=
        arrayListToString( (ArrayList) fData.get(UCMemberProfileCore.ARRAY_LIST_ACCESS)) +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.STRING_REAL_NAME).toString() +
        "\n";
    returnStr +=
        arrayListToString( (ArrayList) fData.get(UCMemberProfileCore.ARRAY_LIST_SYSTEM_PARAMETERS)) +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.INT_SEED).toString() + "\n";
    returnStr += fData.get(UCMemberProfileCore.LONG_INITIAL_CASH).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.LONG_TRADING_UNIT).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.LONG_FEE_PER_UNIT).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.LONG_MARGIN_RATE).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.LONG_MAX_LOAN).toString() + "\n";
    returnStr += fData.get(UCMemberProfileCore.DOUBLE_INTEREST).toString() +
        "\n";
    returnStr += fData.get(UCMemberProfileCore.INT_STATUS).toString() + "\n";
    returnStr += fData.get(UCMemberProfileCore.INT_NO_OF_LOGIN_AGENTS).toString() +
        "\n";
    HashMap yesterdayBalance = (HashMap) fData.get(UCMemberProfileCore.
        HASHMAP_YESTERDAY_BALANCE);
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_INITIAL_CASH).
        toString() + "\n";
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_LOAN).toString() +
        "\n";
    returnStr +=
        yesterdayBalance.get(UCMemberProfileCore.LONG_UNREALIZED_PROFIT).
        toString() + "\n";
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_MARGIN).toString() +
        "\n";
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_SUM_OF_FEE).
        toString() + "\n";
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_SUM_OF_INTEREST).
        toString() + "\n";
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_CASH).toString() +
        "\n";
    returnStr += yesterdayBalance.get(UCMemberProfileCore.LONG_PROFIT).toString() +
        "\n";
    HashMap todayBalance = (HashMap) fData.get(UCMemberProfileCore.
                                               HASHMAP_TODAY_BALANCE);
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_INITIAL_CASH).
        toString() + "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_LOAN).toString() +
        "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_UNREALIZED_PROFIT).
        toString() + "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_MARGIN).toString() +
        "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_SUM_OF_FEE).toString() +
        "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_SUM_OF_INTEREST).
        toString() + "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_CASH).toString() +
        "\n";
    returnStr += todayBalance.get(UCMemberProfileCore.LONG_PROFIT).toString() +
        "\n";
    HashMap position = (HashMap) fData.get(UCMemberProfileCore.HASHMAP_POSITION);
    returnStr +=
        position.get(UCMemberProfileCore.LONG_SUM_OF_SELL_POSITIONS_UNTIL_YESTERDAY).
        toString() + "\n";
    returnStr +=
        position.get(UCMemberProfileCore.LONG_SUM_OF_BUY_POSITIONS_UNTIL_YESTERDAY).
        toString() + "\n";
    returnStr += position.get(UCMemberProfileCore.LONG_TODAY_SELL_POSITIONS).
        toString() + "\n";
    returnStr += position.get(UCMemberProfileCore.LONG_TODAY_BUY_POSITIONS).
        toString() + "\n";
    return returnStr;
  }

  /**
   * ArrayList�^�̏���String�i�R������؂�j�ɂ��ĕԂ��D
   * @param list ArrayList
   * @return�@String
   */
  static public String arrayListToString(ArrayList list) {
    String result = "";
    Iterator itr = list.iterator();
    while (itr.hasNext()) {
      result += (String) itr.next();
      if (itr.hasNext()) {
        result += ":";
      }
    }
    return result;
  }

  /**
   * String�i�R������؂�j�̏���ArrayList�^�ɂ��ĕԂ��D
   * @param str �R������؂�̏��
   * @return�@ArrayList
   */
  static public ArrayList stringToArrayList(String str) {
    ArrayList result = new ArrayList();
    StringTokenizer st = new StringTokenizer(str, ":");
    while (st.hasMoreTokens()) {
      String parameter = st.nextToken();
      result.add(parameter);
    }
    return result;
  }

}