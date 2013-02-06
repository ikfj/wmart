package org.wmart.core;

/**
 * �����s�ꃏ�[�J�[�N���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WAuctioneerSpot extends WAuctioneer {

	/**
	 * �R���X�g���N�^
	 * 
	 */
	public WAuctioneerSpot(WMart master, int maxDate, int sessionsPerDay, int slots) {
		super(master, "spot", maxDate, sessionsPerDay, slots);
	}

	/**
	 * ��菈���X���b�h
	 */
	@Override
	public void run() {
		log.debug("--> " + Thread.currentThread().getName() + " start");
		fBoard.makeContracts(fStatus.getDate(), fStatus.getSession(), getEarliestSlot());
		log.debug("<-- " + Thread.currentThread().getName() + " finish");
	}

	/**
	 * �w�肳�ꂽ���߂ɂ��������Ώۊ��Ԃ̊J�n�������擾
	 */
	@Override
	public int getEarliestSlotAt(int date, int session) {
		// ���X�Z�b�V����
		return (date - 1) * fSessionsPerDay + session + 2;
	}

	/**
	 * �w�肳�ꂽ���߂ɂ��������Ώۊ��Ԃ̏I���������擾
	 */
	@Override
	public int getLatestSlotAt(int date, int session) {
		return getEarliestSlotAt(date, session) + fSlots - 1;
	}

}
