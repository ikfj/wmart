package org.umart.cmdCore;

import java.util.*;

/**
 * @author user
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class UCOrderIDHistoryCore implements ICommand {

  /** �R�}���h�� */
  public static final String CMD_NAME = "OrderIDHistory";

  /** �R�}���h�̎��s���ʂ̏�� */
  protected UCommandStatus fCommandStatus;

  /** ����ID�����𒲂ׂ������[�U�[��ID */
  protected int fTargetUserID;

  /** �ߋ��񂹉��񕪂̒���ID���~�������H */
  protected int fNoOfSteps;

  /** ����ID���� */
  protected ArrayList fOrderIDHistory;

  /**
   * Constructor for UCCOrderIDHistory.
   */
  public UCOrderIDHistoryCore() {
    super();
    fCommandStatus = new UCommandStatus();
    fOrderIDHistory = new ArrayList();
    fTargetUserID = -1;
    fNoOfSteps = 0;
  }

  /**
   * @see org.umart.cmdCore.ICommand#isNameEqualTo(String)
   */
  public boolean isNameEqualTo(String name) {
    if (name.equalsIgnoreCase(CMD_NAME)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @see org.umart.cmdCore.ICommand#getName()
   */
  public String getName() {
    return CMD_NAME;
  }

  /**
   * @see org.umart.cmdCore.ICommand#getResultString()
   */
  public String getResultString() {
    String result = "";
    Iterator itr = fOrderIDHistory.iterator();
    while (itr.hasNext()) {
      Long id = (Long) itr.next();
      result += id.toString() + " ";
    }
    return result;
  }

  public void setArguments(int targetUserID, int noOfSteps) {
    fTargetUserID = targetUserID;
    fNoOfSteps = noOfSteps;
  }

  /**
   * @see org.umart.serverCore.ICCommand#setArguments(StringTokenizer)
   */
  public boolean setArguments(StringTokenizer st) {
    if (st.hasMoreTokens()) {
      fTargetUserID = Integer.parseInt(st.nextToken());
      fNoOfSteps = Integer.parseInt(st.nextToken());
      return true;
    } else {
      return false;
    }
  }

  /**
   * @see org.umart.serverCore.ICCommand#getStatus()
   */
  public UCommandStatus getStatus() {
    return fCommandStatus;
  }

  /**
   * @see org.umart.serverCore.ICCommand#printOn()
   */
  public void printOn() {
    System.out.println(getResultString());
  }

  public ArrayList getOrderIDHistory() {
    return fOrderIDHistory;
  }

}
