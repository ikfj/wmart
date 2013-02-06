/**
 *
 */
package org.wmart.core;

import java.util.*;

import org.umart.serverCore.*;

/**
 * ���ׂĂ̒������Ǘ�����N���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WOrderManager {

	/** ���ʖ� */
	private String fName;
	/** �������� */
	private ArrayList fOrderHistory;
	/** �e���[�U�[�̒������Ǘ�����WOrderArray�I�u�W�F�N�g�̃x�N�^ */
	private Vector fOrderArrays;
	/** ����ID�𔭐����邽�߂̃J�E���^ */
	static private long fOrderID = 1;
	/** ����񂹊��ԓ��̒������\�[�g���邽�߂̗��������� */
	private Random fRandom;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param name
	 *            ���ʖ�
	 */
	public WOrderManager() {
		fOrderArrays = new Vector();
		fOrderHistory = new ArrayList();
		fRandom = URandom.getInstance();
	}

	/**
	 * �����Ŏw�肳�ꂽ������V���ɍ쐬����
	 * 
	 * @param userID
	 *            ���[�U�[ID
	 * @param userName
	 *            ���[�U��
	 * @param brandName
	 *            ���i�� (�s�g�p)
	 * @param auctioneerName
	 *            �s�ꖼ
	 * @param sellBuy
	 *            �����敪 (��: WOrder.SELL, ��: WOrder.BUY)
	 * @param marketLimit
	 *            ���s�w�l�敪 (���s: WOrder.MARKET, �w�l: WOrder.LIMIT)
	 * @param price
	 *            �������i
	 * @param spec
	 *            �������� ("���i��:��]����:�ő�����:�Œx����:���׎���;" �̌J��Ԃ�)
	 * @param date
	 *            ������
	 * @param session
	 *            ������
	 * @return ���� (�����ȏꍇ�� null ���Ԃ�)
	 */
	public WOrder createOrder(int userID, String userName, String brandName, String auctioneerName, int sellBuy,
		int marketLimit, long price, String spec, int date, int session) {
		WOrder o = new WOrder();
		o.setUserID(userID);
		o.setUserName(userName);
		o.setBrandName(brandName);
		o.setAuctioneerName(auctioneerName);
		o.setOrderID(++fOrderID);
		o.setTime(new Date());
		o.setDate(date);
		o.setSession(session);
		o.setSellBuy(sellBuy);
		o.setMarketLimit(marketLimit);
		if (marketLimit == WOrder.MARKET) {
			o.setOrderPrice(0);
		} else {
			o.setOrderPrice(price);
		}
		int nGoods = o.setOrderSpec(spec);
		o.setRandomNumber(this.getRandomInteger());
		if (sellBuy == WOrder.SELL && nGoods > 1) {
			o = null; // ���蒍����1���i�Ɍ���
		}
		return o;
	}

	/**
	 * ���̐����̗�����Ԃ��D
	 * 
	 * @return ���̐����̗���
	 */
	public int getRandomInteger() {
		return Math.abs(fRandom.nextInt());
	}

	/**
	 * userID���������o�[�̑S�Ă̒������������D
	 * 
	 * @param userID
	 *            ���������������������o�[�̃��[�U�[ID
	 */
	public void cancelAllOrdersOfMember(int userID) {
		WOrderArray orderArray = getOrderArray(userID);
		if (orderArray == null) {
			System.out.print("Member" + userID + " cannot be found ");
			System.out.println("in WOrderManager.cancelAllOrdersOfMember");
			return;
		}
		Enumeration orders = orderArray.getUncontractedOrders().elements();
		while (orders.hasMoreElements()) {
			WOrder o = (WOrder) orders.nextElement();
			o.cancel();
			if (o.getContractVolume() > 0) {
				orderArray.getContractedOrders().addElement(o);
			}
		}
		orderArray.getUncontractedOrders().removeAllElements();
	}

	/**
	 * userID���������o�[�̑S�Ă̒�����Vector�ɓ���ĕԂ��D
	 * 
	 * @param userID
	 *            ���[�U�[ID
	 * @return �w�肳�ꂽ�����o�[�̑S�Ă̒������܂�Vector
	 */
	public Vector getAllOrders(int userID) {
		Vector result = new Vector();
		WOrderArray orderArray = getOrderArray(userID);
		if (orderArray == null) {
			System.out.print("Member" + userID + " cannot be found ");
			System.out.println("in WOrderManager.getExecutions");
			return result;
		}
		Enumeration contOrders = orderArray.getContractedOrders().elements();
		while (contOrders.hasMoreElements()) {
			WOrder o = (WOrder) contOrders.nextElement();
			result.addElement(o);
		}
		Enumeration uncontOrders = orderArray.getUncontractedOrders().elements();
		while (uncontOrders.hasMoreElements()) {
			WOrder o = (WOrder) uncontOrders.nextElement();
			result.addElement(o);
		}
		return result;
	}

	/**
	 * userID���������o�[��orderID�Ŏw�肳��钍�����������D
	 * 
	 * @param userID
	 *            ���[�U�[ID
	 * @param orderID
	 *            ����ID
	 * @return �������ꂽ�����D�w�肵�����������݂��Ȃ��ꍇ�Cnull��Ԃ��D
	 */
	public WOrder cancelOrder(int userID, long orderID) {
		// �w�肳�ꂽorderID�̒�����������Ȃ����null��Ԃ�
		WOrderArray orderArray = getOrderArray(userID);
		if (orderArray == null) {
			System.out.print("Member" + userID + " cannot be found ");
			System.out.println("in WOrderManager.cancelOrder");
			return null;
		}
		Vector uncontractedOrders = orderArray.getUncontractedOrders();
		int size = uncontractedOrders.size();
		int targetIndex = -1;
		for (int i = 0; i < size; ++i) {
			WOrder o = (WOrder) uncontractedOrders.elementAt(i);
			if (o.getOrderID() == orderID) {
				targetIndex = i;
				break;
			}
		}
		if (targetIndex < 0) {
			return null;
		}
		WOrder order = (WOrder) uncontractedOrders.remove(targetIndex);
		order.cancel();
		if (order.getContractVolume() > 0) {
			orderArray.getContractedOrders().addElement(order);
		}
		return order;
	}

	/**
	 * ���ׂẴ����o�[�̑S�Ă̒�������菜���D
	 */
	public void removeAllOrders() {
		Enumeration e = fOrderArrays.elements();
		while (e.hasMoreElements()) {
			WOrderArray array = (WOrderArray) e.nextElement();
			array.removeAllContractedOrders();
			array.removeAllUncontractedOrders();
		}
	}

	/**
	 * �S�����̗�������Y��������Ԃ��D
	 * 
	 * @param orderID
	 *            ����ID
	 * @return ����
	 */
	public WOrder getOrderFromHistory(long orderID) {
		int index = (int) orderID - 1;
		if (index < 0 || index >= fOrderHistory.size()) {
			return null;
		}
		return (WOrder) fOrderHistory.get(index);
	}

	/**
	 * �����𗚗��֓o�^����D
	 * 
	 * @param o
	 *            ����
	 */
	public void registerOrderToHistory(WOrder o) {
		fOrderHistory.add(o);
		WOrderArray orderArray = getOrderArray(o.getUserID());
		orderArray.getHistory().add(o);
	}

	/**
	 * userID�̒����Ǘ��N���XWOrderArray�𐶐�����D
	 * 
	 * @param userID
	 *            ���[�U�[ID
	 * @return �����Ftrue, ���s: false (���ꃁ���o�[�����݂���ꍇ)
	 */
	public boolean createOrderArray(int userID) {
		Enumeration e = fOrderArrays.elements();
		while (e.hasMoreElements()) {
			WOrderArray x = (WOrderArray) e.nextElement();
			if (x.getUserID() == userID) {
				System.err.println("The same UserID exists.");
				return false;
			}
		}
		fOrderArrays.addElement(new WOrderArray(userID));
		return true;
	}

	/**
	 * ������ǉ�����D
	 * 
	 * @param o
	 *            �ǉ�����
	 * @return �����Ftrue, ���s: false (�Ή����郁���o�[��������Ȃ��Ƃ�)
	 */
	public boolean addOrder(WOrder o) {
		Enumeration e = fOrderArrays.elements();
		while (e.hasMoreElements()) {
			WOrderArray x = (WOrderArray) e.nextElement();
			if (x.getUserID() == o.getUserID()) {
				x.addOrder(o);
				return true;
			}
		}
		return false;
	}

	/**
	 * �����Ŏw�肳�ꂽ�����o�[�̒����W����Ԃ��D
	 * 
	 * @param userID
	 *            ���[�U�[ID
	 * @return �����W�����Ǘ�����WOrderArray�I�u�W�F�N�g�D �Ή����郁���o�[��WOrderArray�I�u�W�F�N�g�� ���݂��Ȃ��ꍇ, null��Ԃ��̂Œ��ӂ��邱�ƁD
	 */
	public WOrderArray getOrderArray(int userID) {
		Enumeration e = fOrderArrays.elements();
		while (e.hasMoreElements()) {
			WOrderArray x = (WOrderArray) e.nextElement();
			if (x.getUserID() == userID) {
				return x;
			}
		}
		return null;
	}

	/**
	 * ���l���̃����o�[��WOrderArray���Ǘ����Ă��邩�Ԃ��D
	 * 
	 * @return �Ǘ����Ă���WOrderArray�̐�
	 */
	public int getNoOfOrderArrays() {
		return fOrderArrays.size();
	}

	/**
	 * �Ǘ����Ă���WOrderArray��񋓂��邽�߂�Enumeration��Ԃ��D
	 * 
	 * @return �Ǘ����Ă���WOrderArray��񋓂��邽�߂�Enumeration
	 */
	public Enumeration getOrderArrays() {
		return fOrderArrays.elements();
	}

	/**
	 * ���݈ʒu���L������
	 */
	public void markCurrentIndex() {
		Enumeration e = fOrderArrays.elements();
		while (e.hasMoreElements()) {
			WOrderArray array = (WOrderArray) e.nextElement();
			array.resetIndexOfLatestContractedOrder();
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return fName;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		fName = name;
	}

	/*
	 * (�� Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("WOrderManager(%s) [OrderArrays=%s]", fName, fOrderArrays);
	}
}
