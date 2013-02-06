package org.wmart.core;

import org.apache.log4j.Logger;
import org.umart.serverCore.UReadWriteLock;

/**
 * 取引所ワーカークラス (先物市場や現物市場など個々の市場メカニズムを実装する)
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
@SuppressWarnings("finally")
public abstract class WAuctioneer implements Runnable {

	/** ロガー */
	protected static Logger log = Logger.getLogger(WAuctioneer.class);
	/** 識別名 */
	protected String fName = "";
	/** サーバー運用日数 */
	protected int fMaxDate = 0;
	/** 1日あたりの板寄せ回数 */
	protected int fSessionsPerDay = 0;
	/** スロット数 */
	protected int fSlots = 0;
	/** マスター */
	protected WMart fMaster = null;
	/** 板 */
	protected WBoard fBoard = null;
	/** ワーカーの状態 */
	protected WServerStatusWorker fStatus = null;
	/** 状態遷移ロック */
	protected UReadWriteLock fStateLock = null;
	/** 約定処理スレッド */
	protected Thread fThread = null;

	/**
	 * コンストラクタ
	 */
	public WAuctioneer(WMart master, String name, int maxDate, int sessionsPerDay, int slots) {
		fName = name;
		fMaxDate = maxDate;
		fSessionsPerDay = sessionsPerDay;
		fSlots = slots;
		fMaster = master;
	}

	/**
	 * 初期化
	 */
	public void initialize(String name) {
		assert (name == fName);
		fBoard = new WBoard(fSlots);
		fStatus = new WServerStatusWorker(fName);
		fStateLock = new UReadWriteLock();
		fStatus.setState(WServerStatusWorker.INITIALIZED);
	}

	/**
	 * 板をリセットする
	 */
	public WBoard resetBoard() {
		fBoard = new WBoard(fSlots);
		return fBoard;
	}

	/**
	 * 板を消す
	 */
	public void flushBoard() {
		this.fBoard = null;
	}

	/**
	 * 約定開始
	 * 
	 * @param date
	 *            ワーカー日
	 * @param session
	 *            ワーカー節
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
	 * 約定終了
	 * 
	 * @return ワーカー通算板寄せ回数 (初回の空振り時は0になるはず)
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
	 * 約定処理スレッドを起動する
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
	 * 約定処理スレッドの終了を待つ
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
	 * 約定処理スレッド
	 */
	public void run() {
		log.info("==> " + Thread.currentThread().getName() + " start");
		fBoard.makeContracts(fStatus.getDate(), fStatus.getSession(), getEarliestSlot());
		log.info("<== " + Thread.currentThread().getName() + " finish");
	}

	/**
	 * 現在のワーカー状態を返す
	 * 
	 * @return ワーカー状態
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
	 * ワーカーの日節 (通算ステップ数) を返す
	 * 
	 * @return
	 */
	public int getStep() {
		return fStatus.getDate() * fSessionsPerDay + fStatus.getSession();
	}

	/**
	 * 現在処理中の取引対象期間の開始時刻を取得
	 */
	public int getEarliestSlot() {
		return getEarliestSlotAt(fStatus.getDate(), fStatus.getSession());
	}

	/**
	 * 現在処理中の取引対象期間の終了時刻を取得
	 */
	public int getLatestSlot() {
		return getLatestSlotAt(fStatus.getDate(), fStatus.getSession());
	}

	/**
	 * 指定された日節における取引対象期間の開始時刻を取得
	 */
	public abstract int getEarliestSlotAt(int date, int session);

	/**
	 * 指定された日節における取引対象期間の終了時刻を取得
	 */
	public abstract int getLatestSlotAt(int date, int session);

	/**
	 * ワーカーの日を返す
	 * 
	 * @return
	 */
	public int getDate() {
		return fStatus.getDate();
	}

	/**
	 * ワーカーの節を返す
	 * 
	 * @return
	 */
	public int getSession() {
		return fStatus.getSession();
	}

	/**
	 * 板を返す
	 * 
	 * @return the board
	 */
	public WBoard getBoard() {
		return fBoard;
	}

	/**
	 * 状態遷移ロックを返す
	 * 
	 * @return the stateLock
	 */
	public UReadWriteLock getStateLock() {
		return fStateLock;
	}

}
