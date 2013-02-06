package org.wmart.core;

import org.apache.log4j.Logger;
import org.umart.serverCore.UReadWriteLock;

/**
 * ��������[�J�[�N���X (�敨�s��⌻���s��ȂǌX�̎s�ꃁ�J�j�Y������������)
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
@SuppressWarnings("finally")
public abstract class WAuctioneer implements Runnable {

	/** ���K�[ */
	protected static Logger log = Logger.getLogger(WAuctioneer.class);
	/** ���ʖ� */
	protected String fName = "";
	/** �T�[�o�[�^�p���� */
	protected int fMaxDate = 0;
	/** 1��������̔񂹉� */
	protected int fSessionsPerDay = 0;
	/** �X���b�g�� */
	protected int fSlots = 0;
	/** �}�X�^�[ */
	protected WMart fMaster = null;
	/** �� */
	protected WBoard fBoard = null;
	/** ���[�J�[�̏�� */
	protected WServerStatusWorker fStatus = null;
	/** ��ԑJ�ڃ��b�N */
	protected UReadWriteLock fStateLock = null;
	/** ��菈���X���b�h */
	protected Thread fThread = null;

	/**
	 * �R���X�g���N�^
	 */
	public WAuctioneer(WMart master, String name, int maxDate, int sessionsPerDay, int slots) {
		fName = name;
		fMaxDate = maxDate;
		fSessionsPerDay = sessionsPerDay;
		fSlots = slots;
		fMaster = master;
	}

	/**
	 * ������
	 */
	public void initialize(String name) {
		assert (name == fName);
		fBoard = new WBoard(fSlots);
		fStatus = new WServerStatusWorker(fName);
		fStateLock = new UReadWriteLock();
		fStatus.setState(WServerStatusWorker.INITIALIZED);
	}

	/**
	 * �����Z�b�g����
	 */
	public WBoard resetBoard() {
		fBoard = new WBoard(fSlots);
		return fBoard;
	}

	/**
	 * ������
	 */
	public void flushBoard() {
		this.fBoard = null;
	}

	/**
	 * ���J�n
	 * 
	 * @param date
	 *            ���[�J�[��
	 * @param session
	 *            ���[�J�[��
	 */
	public void startAuction(int date, int session) {
		try {
			fStateLock.writeLock();
			fStatus.setDate(date);
			fStatus.setSession(session);
			fStatus.setState(WServerStatusWorker.PREPARING);
			startThread();
			fStatus.setState(WServerStatusWorker.BEFORE_CONCLUSION);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(5);
		} finally {
			fStateLock.writeUnlock();
		}
	}

	/**
	 * ���I��
	 * 
	 * @return ���[�J�[�ʎZ�񂹉� (����̋�U�莞��0�ɂȂ�͂�)
	 */
	public int finishAuction() {
		try {
			fStateLock.writeLock();
			fStatus.setState(WServerStatusWorker.CONCLUDING);
			joinThread();
			fStatus.setState(WServerStatusWorker.AFTER_CONCLUSION);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(5);
		} finally {
			fStateLock.writeUnlock();
			return getStep();
		}
	}

	/**
	 * ��菈���X���b�h���N������
	 */
	private void startThread() {
		String threadName = fName + " auction for date" + fStatus.getDate() + "/session"
			+ fStatus.getSession();
		if (fThread != null && fThread.isAlive()) {
			System.err.println("Attempt to run " + threadName
				+ " while running another thread in WAuctioneer.start_thread()");
			System.exit(5);
		}
		fThread = new Thread(this, threadName);
		fThread.start();
	}

	/**
	 * ��菈���X���b�h�̏I����҂�
	 */
	private Boolean joinThread() {
		if (fThread == null) {
			return false;
		}
		try {
			fThread.join();
		} catch (InterruptedException e) {
		}
		return true;
	}

	/**
	 * ��菈���X���b�h
	 */
	public void run() {
		log.info("==> " + Thread.currentThread().getName() + " start");
		fBoard.makeContracts(fStatus.getDate(), fStatus.getSession(), getEarliestSlot());
		log.info("<== " + Thread.currentThread().getName() + " finish");
	}

	/**
	 * ���݂̃��[�J�[��Ԃ�Ԃ�
	 * 
	 * @return ���[�J�[���
	 */
	public WServerStatusWorker getStatus() {
		WServerStatusWorker result = null;
		try {
			fStateLock.readLock();
			result = (WServerStatusWorker) fStatus.clone();
		} catch (Exception ex) {
			System.err.println("Exception: " + ex + " in WAuctioneer.getStatus");
			System.exit(5);
		} finally {
			fStateLock.readUnlock();
			return result;
		}
	}

	/**
	 * ���[�J�[�̓��� (�ʎZ�X�e�b�v��) ��Ԃ�
	 * 
	 * @return
	 */
	public int getStep() {
		return fStatus.getDate() * fSessionsPerDay + fStatus.getSession();
	}

	/**
	 * ���ݏ������̎���Ώۊ��Ԃ̊J�n�������擾
	 */
	public int getEarliestSlot() {
		return getEarliestSlotAt(fStatus.getDate(), fStatus.getSession());
	}

	/**
	 * ���ݏ������̎���Ώۊ��Ԃ̏I���������擾
	 */
	public int getLatestSlot() {
		return getLatestSlotAt(fStatus.getDate(), fStatus.getSession());
	}

	/**
	 * �w�肳�ꂽ���߂ɂ��������Ώۊ��Ԃ̊J�n�������擾
	 */
	public abstract int getEarliestSlotAt(int date, int session);

	/**
	 * �w�肳�ꂽ���߂ɂ��������Ώۊ��Ԃ̏I���������擾
	 */
	public abstract int getLatestSlotAt(int date, int session);

	/**
	 * ���[�J�[�̓���Ԃ�
	 * 
	 * @return
	 */
	public int getDate() {
		return fStatus.getDate();
	}

	/**
	 * ���[�J�[�̐߂�Ԃ�
	 * 
	 * @return
	 */
	public int getSession() {
		return fStatus.getSession();
	}

	/**
	 * ��Ԃ�
	 * 
	 * @return the board
	 */
	public WBoard getBoard() {
		return fBoard;
	}

	/**
	 * ��ԑJ�ڃ��b�N��Ԃ�
	 * 
	 * @return the stateLock
	 */
	public UReadWriteLock getStateLock() {
		return fStateLock;
	}

}
