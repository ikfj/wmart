package org.wmart.analyzer;

import java.util.*;

/**
 * ������̃^�C�����C����\���N���X (�r�W���A���C�U�p�A�z��x�[�X)
 * 
 * ��f�[�^��2�d�̃����N���X�g����Ȃ�B
 * <ul>
 * <li>�����̃��X�g�͒������݂��ɏd�Ȃ�Ȃ��悤�Ƀv���b�g���ꂽ1�{�̃^�C�����C����\���B</li>
 * <li>�O���̃��X�g�͎��Ԃ��d�Ȃ钍���𕪂��邽�߂ɍ��ꂽ�����̃^�C�����C����\���B</li>
 * </ul>
 * ���f�[�^��1�̃c���[�}�b�v����Ȃ�A���ׂĂ̒�����ێ�����B
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WTimelineBuyerArray {

	/** ���O */
	private String fName;
	/** ��f�[�^ */
	private LinkedList<LinkedList<WOrderBuyer>> fOrderList;
	/** ���f�[�^ */
	private TreeMap<String, WOrderBuyer> fOrderMap;

	/**
	 * �R���X�g���N�^
	 */
	public WTimelineBuyerArray(String username) {
		fName = username;
		fOrderList = new LinkedList<LinkedList<WOrderBuyer>>();
		fOrderMap = new TreeMap<String, WOrderBuyer>();
	}

	/**
	 * �����񂩂�ǉ��ǂݍ���
	 * 
	 * @param encodedSpecs
	 *            "���i��:��]����:�ő�����:�Œx����:���׎���;" �̌J��Ԃ�
	 * @param contracted
	 *            ���ς݃t���O
	 */
	public void append(String encodedSpecs, boolean contracted) {
		WOrderBuyer order = new WOrderBuyer(encodedSpecs, contracted);
		String specStr = order.getSpec(); // �\�[�g�����
		assert (!fOrderMap.containsKey(specStr)) : "order " + specStr + " exists";
		fOrderMap.put(specStr, order);
		availableList(order.getEarliest(), order.getLatest()).add(order);
	}

	/**
	 * �����񂩂�㏑���ǂݍ��݁A���ς݃t���O���X�V
	 * 
	 * @param encodedSpecs
	 *            "���i��:��]����:�ő�����:�Œx����:���׎���;" �̌J��Ԃ�
	 * @param contracted
	 *            ���ς݃t���O
	 */
	public void overread(String encodedSpecs, boolean contracted) {
		WOrderBuyer order = new WOrderBuyer(encodedSpecs, contracted);
		String specStr = order.getSpec(); // �\�[�g�����
		assert (fOrderMap.containsKey(specStr)) : "order " + specStr + " doesn't exist";
		fOrderMap.get(specStr).setContracted(contracted);
	}

	/**
	 * �����̒������ԑтƏd�Ȃ�Ȃ��^�C�����C����Ԃ�
	 */
	private LinkedList<WOrderBuyer> availableList(int earliest, int latest) {
		for (Iterator<LinkedList<WOrderBuyer>> itr1 = fOrderList.iterator(); itr1.hasNext();) {
			LinkedList<WOrderBuyer> sublist = itr1.next();
			Boolean ok = true;
			for (Iterator<WOrderBuyer> itr2 = sublist.iterator(); itr2.hasNext();) {
				WOrderBuyer order = itr2.next();
				for (Iterator<WOrderSpec> itr3 = order.iterator(); itr3.hasNext();) {
					WOrderSpec spec = itr3.next();
					if (earliest <= spec.getDeadlineTime() && latest >= spec.getArrivalTime()) {
						ok = false;
					}
				}
				if (!ok) {
					break;
				}
			}
			if (ok) {
				return sublist;
			}
		}
		LinkedList<WOrderBuyer> sublist = new LinkedList<WOrderBuyer>();
		fOrderList.add(sublist);
		return sublist;
	}

	/**
	 * ���O���擾���܂��B
	 * 
	 * @return ���O
	 */
	public String getName() {
		return fName;
	}

	/**
	 * �������X�g���擾���܂��B
	 * 
	 * @return �������X�g
	 */
	public LinkedList<LinkedList<WOrderBuyer>> getOrderList() {
		return fOrderList;
	}

	/**
	 * �������X�g�̃C�e���[�^���擾���܂��B
	 * 
	 * @return �����̃C�e���[�^
	 */
	public Iterator<LinkedList<WOrderBuyer>> iterator() {
		return fOrderList.iterator();
	}

	/**
	 * ����e�X�g�p
	 */
	public static void main(String argv[]) {
		WTimelineBuyerArray tl = new WTimelineBuyerArray("test");
		tl.append("B:5:10:12:3;E:11:13:13:1;D:2:14:16:2;", false); // 10-15
		tl.append("D:17:16:16:1;C:19:17:19:3;B:13:20:20:1;", false); // 16-20
		// tl.append("E:15:13:13:1;A:11:14:14:1;D:7:15:16:2;",false); // 13-16
		// tl.append("A:14:24:25:2;B:15:26:29:1;D:16:26:29:3;C:17:26:29:4;E:16:30:32:3;",false); //
		// 24-32
		System.out.println(tl.getOrderList().toString());
		tl.overread("B:5:10:12:3;E:11:13:13:1;D:2:14:16:2;", true); // 10-15
		tl.overread("D:17:16:16:1;C:19:17:19:3;B:13:20:20:1;", true); // 16-20
		System.out.println(tl.getOrderList().toString());
	}
}
