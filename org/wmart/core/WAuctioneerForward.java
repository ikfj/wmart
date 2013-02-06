package org.wmart.core;

/**
 * 先物市場ワーカークラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WAuctioneerForward extends WAuctioneer {

	/**
	 * コンストラクタ
	 * 
	 */
	public WAuctioneerForward(WMart master, int maxDate, int sessionsPerDay, int slots) {
		super(master, "forward", maxDate, sessionsPerDay, slots);
	}

	/**
	 * 約定処理スレッド
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
	 * 指定された日節における取引対象期間の開始時刻を取得
	 */
	@Override
	public int getEarliestSlotAt(int date, int session) {
		// 翌々日の最初
		return (date + 1) * fSessionsPerDay + 1;
	}

	/**
	 * 指定された日節における取引対象期間の終了時刻を取得
	 */
	@Override
	public int getLatestSlotAt(int date, int session) {
		return getEarliestSlotAt(date, session) + fSlots - 1;
	}

}
