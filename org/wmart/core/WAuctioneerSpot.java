package org.wmart.core;

/**
 * 現物市場ワーカークラス
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WAuctioneerSpot extends WAuctioneer {

	/**
	 * コンストラクタ
	 * 
	 */
	public WAuctioneerSpot(WMart master, int maxDate, int sessionsPerDay, int slots) {
		super(master, "spot", maxDate, sessionsPerDay, slots);
	}

	/**
	 * 約定処理スレッド
	 */
	@Override
	public void run() {
		log.debug("--> " + Thread.currentThread().getName() + " start");
		fBoard.makeContracts(fStatus.getDate(), fStatus.getSession(), getEarliestSlot());
		log.debug("<-- " + Thread.currentThread().getName() + " finish");
	}

	/**
	 * 指定された日節における取引対象期間の開始時刻を取得
	 */
	@Override
	public int getEarliestSlotAt(int date, int session) {
		// 翌々セッション
		return (date - 1) * fSessionsPerDay + session + 2;
	}

	/**
	 * 指定された日節における取引対象期間の終了時刻を取得
	 */
	@Override
	public int getLatestSlotAt(int date, int session) {
		return getEarliestSlotAt(date, session) + fSlots - 1;
	}

}
