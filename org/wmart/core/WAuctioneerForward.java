package org.wmart.core;

/**
 * �敨�s�ꃏ�[�J�[�N���X
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WAuctioneerForward extends WAuctioneer {

	/**
	 * �R���X�g���N�^
	 * 
	 */
	public WAuctioneerForward(WMart master, int maxDate, int sessionsPerDay, int slots) {
		super(master, "forward", maxDate, sessionsPerDay, slots);
	}

	/**
	 * ��菈���X���b�h
	 */
	@Override
	public void run() {
		log.debug("--> " + Thread.currentThread().getName() + " start");
		fBoard.makeContracts(fStatus.getDate(), fStatus.getSession(), getEarliestSlot());
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		log.debug("<-- " + Thread.currentThread().getName() + " finish");
	}

	/**
	 * �w�肳�ꂽ���߂ɂ��������Ώۊ��Ԃ̊J�n�������擾
	 */
	@Override
	public int getEarliestSlotAt(int date, int session) {
		// ���X���̍ŏ�
		return (date + 1) * fSessionsPerDay + 1;
	}

	/**
	 * �w�肳�ꂽ���߂ɂ��������Ώۊ��Ԃ̏I���������擾
	 */
	@Override
	public int getLatestSlotAt(int date, int session) {
		return getEarliestSlotAt(date, session) + fSlots - 1;
	}

}
