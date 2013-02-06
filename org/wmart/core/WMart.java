package org.wmart.core;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.umart.cmdCore.ICommand;
import org.umart.serverCore.*;
import org.wmart.agent.WBaseAgent;
import org.wmart.cmdClientSA.WProtocolForLocalClient;
import org.wmart.cmdCore.*;
import org.wmart.logger.*;

/**
 * 取引所マスタークラス (証券会社相当)
 * 
 * @author Ikki Fujiwara, NII
 */
@SuppressWarnings("finally")
public class WMart {

	/** スーパーユーザーのID */
	public static int SU_ID;
	/** サーバー運用日数 */
	private int fMaxDate;
	/** 1日あたりの板寄せ回数 */
	private int fSessionsPerDay;
	/** 現物取引対象スロット数 */
	private int fSlotsSpot;
	/** 先物取引対象スロット数 */
	private int fSlotsForward;
	/** ワークフローの最大長さ */
	private int fMaxLength;

	/** 会員情報 */
	private UMemberList fMemberLog;
	/** エージェントの配列 */
	private ArrayList fStrategyArray;
	/** クライアントのログイン管理 */
	private ULoginManager fLoginManager;
	/** 口座管理 */
	private WAccountManager fAccountManager;
	/** コマンド履歴 */
	private WOrderCommandLog fOrderCommandLog;

	/** ワーカー */
	private TreeMap<String, WAuctioneer> fAuctioneers;
	/** 注文管理 */
	private TreeMap<String, WOrderManager> fOrderManagers;
	/** 価格系列 */
	private TreeMap<String, WPriceInfoDB> fPriceInfoDBs;

	/** マスターの状態 */
	private WServerStatusMaster fStatus;
	/** マスターの状態遷移ロック */
	private UReadWriteLock fStateLock;

	/** ログを生成しない */
	public static final int NO_LOG = 0;
	/** 詳細ログを生成する */
	public static final int DETAILED_LOG = 1;
	/** 簡易ログを生成する */
	public static final int SIMPLE_LOG = 2;
	/** ログの生成方法(NO_LOG or DETAILED_LOG or SIMPLE_LOG)の指定 */
	private int fLogFlag;
	/** ログ出力先ディレクトリ名 */
	private String fLogDir;
	/** 注文依頼・取消ログ名 */
	public final static String ORDER_LOG_DIR = "order";
	/** 約定情報ログ名 */
	public final static String EXECUTION_LOG_DIR = "execution";
	/** 口座情報ログ名 */
	public final static String ACCOUNT_LOG_DIR = "account";
	/** 資源情報ログ名 */
	public final static String RESOURCE_LOG_DIR = "resource";

	/** コマンド実行可能チェッカー */
	private WCmdExecutableChecker fCmdExecutableChecker;

	/** ロガー */
	private static Logger log = Logger.getLogger(WMart.class);

	/**
	 * コンストラクタ
	 */
	public WMart(UMemberList members, long seed, int maxDate, int sessionsPerDay, int slotsForward,
		int maxLength) {
		fMemberLog = members;
		URandom.setSeed(seed);
		fMaxDate = maxDate;
		fSessionsPerDay = sessionsPerDay;
		fSlotsSpot = 1;
		fSlotsForward = slotsForward;
		fMaxLength = maxLength;

		fStrategyArray = new ArrayList();
		fAccountManager = new WAccountManager(members, fSessionsPerDay);
		fOrderCommandLog = new WOrderCommandLog();

		fOrderManagers = new TreeMap<String, WOrderManager>();
		fOrderManagers.put("spot", new WOrderManager());
		fOrderManagers.put("forward", new WOrderManager());
		setupOrderManagers();

		fAuctioneers = new TreeMap<String, WAuctioneer>();
		fAuctioneers.put("spot", new WAuctioneerSpot(this, fMaxDate, fSessionsPerDay, fSlotsSpot));
		fAuctioneers.put("forward", new WAuctioneerForward(this, fMaxDate, fSessionsPerDay,
			fSlotsForward));
		setupAuctioneers();

		fPriceInfoDBs = new TreeMap<String, WPriceInfoDB>();
		fPriceInfoDBs.put("spot", new WPriceInfoDB(fSlotsSpot, fSessionsPerDay * fMaxDate));
		fPriceInfoDBs.put("forward", new WPriceInfoDB(fSlotsForward, fMaxDate));
		setupPriceInfoDBs();

		fStatus = new WServerStatusMaster("master");
		fStateLock = new UReadWriteLock();
		fStatus.setState(WServerStatusMaster.BEFORE_TRADING);

		fLoginManager = createLoginManager();
		setupLoginManager();

		fCmdExecutableChecker = new WCmdExecutableChecker();
		setupCmdExecutableChecker("/org/wmart/cmdCore/SVMP.csv");

		fLogFlag = WMart.NO_LOG;
	}

	/**
	 * ローカルエージェントから注文を受けつける
	 */
	public void doActionsByLocalAgents() {
		Iterator itr = fStrategyArray.iterator();
		while (itr.hasNext()) {
			WBaseAgent agent = (WBaseAgent) itr.next();
			// System.err.println(org.umart.strategy.getLoginName());
			int date = fStatus.getDate();
			int session = fStatus.getSession();
			int state = fStatus.getState();
			agent.doActions(date, session, state, fMaxDate, fSessionsPerDay, fSlotsForward,
				fMaxLength);
		}
	}

	/**
	 * 時刻を進める
	 * 
	 * @return マスター状態
	 */
	public WServerStatusMaster nextStatus() {

		try {
			fStateLock.writeLock();
			int date = fStatus.getDate();
			int session = fStatus.getSession();
			if (fAuctioneers.containsKey("spot") || session == fSessionsPerDay) {
				System.out.println(String.format("====== date%d/session%d %s ======", date,
					session, fStatus.getStateString()));
			}
			switch (fStatus.getState()) {

			case WServerStatusMaster.BEFORE_TRADING:
				// 最初のセッションへ
				fStatus.setDate(1);
				fStatus.setSession(1);
				fStatus.setState(WServerStatusMaster.BUSINESS_HOURS);
				break;

			case WServerStatusMaster.BUSINESS_HOURS:
				// セッションの前半
				fStatus.setState(WServerStatusMaster.CONCLUDING_AUCTIONS);
				if (fAuctioneers.containsKey("spot")) {
					finishAuction("spot", date, session);
				}
				if (fAuctioneers.containsKey("forward")
					&& session == fSessionsPerDay - fSlotsSpot - 1) {
					finishAuction("forward", date, session);
				}
				updateLog(fStatus.getDate(), fStatus.getSession(), fStatus.getState());
				fStatus.setState(WServerStatusMaster.AFTER_HOURS);
				break;

			case WServerStatusMaster.AFTER_HOURS:
				// セッションの後半
				fStatus.setState(WServerStatusMaster.PREPARING_AUCTIONS);
				if (fAuctioneers.containsKey("spot")) {
					startAuction("spot", date, session);
				}
				if (fAuctioneers.containsKey("forward") && session == fSessionsPerDay) {
					startAuction("forward", date, session);
				}
				updateLog(fStatus.getDate(), fStatus.getSession(), fStatus.getState());
				// 次のセッションへ
				if (fStatus.getSession() == fSessionsPerDay) {
					fStatus.incrementDate();
					fStatus.resetSession();
				} else {
					fStatus.incrementSession();
				}
				if (fStatus.getDate() <= fMaxDate) {
					fStatus.setState(WServerStatusMaster.BUSINESS_HOURS);
				} else {
					fStatus.setState(WServerStatusMaster.AFTER_TRADING);
				}
				break;

			case WServerStatusMaster.AFTER_TRADING:
				// 最終セッションの後
				if (fAuctioneers.containsKey("spot")) {
					finishAuction("spot", date, session);
				}
				if (fAuctioneers.containsKey("forward")) {
					finishAuction("forward", date, session);
				}
				updateLog(fStatus.getDate(), fStatus.getSession(), fStatus.getState());
				fStatus.setState(WServerStatusMaster.SHUTDOWN);
				break;

			default:
				System.err.println("Unknown status " + fStatus.getStateString() + " in WMart");
				System.exit(5);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(5);
		} finally {
			fStateLock.writeUnlock();
			return (WServerStatusMaster) fStatus.clone();
		}
	}

	/**
	 * 約定処理を開始する
	 * 
	 * @param auctioneerName
	 *            市場名
	 * @param date
	 *            現在の日
	 * @param session
	 *            現在の節
	 * @throws IOException
	 */
	private void startAuction(String auctioneerName, int date, int session) throws IOException {
		WAuctioneer au = fAuctioneers.get(auctioneerName);
		// 板を新しくする
		WBoard board = au.resetBoard();
		WOrderManager om = fOrderManagers.get(auctioneerName);
		// オーダーを OrderManager から Board へ移す
		log.trace(om.toString());
		if (moveUncontractedOrdersToBoard(om, board)) {
			au.startAuction(date, session);
		}
	}

	/**
	 * 全部未約定のオーダーを OrderManager から Board へ移動する
	 */
	private boolean moveUncontractedOrdersToBoard(WOrderManager om, WBoard board) {
		int sellOrderCount = 0;
		int buyOrderCount = 0;
		Enumeration arrays = om.getOrderArrays();
		while (arrays.hasMoreElements()) {
			WOrderArray orderArray = (WOrderArray) arrays.nextElement();
			Vector uncontractedOrderArray = orderArray.getUncontractedOrders();
			Enumeration orders = uncontractedOrderArray.elements();
			while (orders.hasMoreElements()) {
				WOrder o = (WOrder) orders.nextElement();
				board.appendOrder(o);
				if (o.getSellBuy() == WOrder.SELL) {
					sellOrderCount++;
				}
				if (o.getSellBuy() == WOrder.BUY) {
					buyOrderCount++;
				}
			}
			orderArray.removeAllUncontractedOrders();
		}
		return (sellOrderCount > 0 && buyOrderCount > 0);
	}

	/**
	 * 約定処理を完了する
	 * 
	 * @param auctioneerName
	 *            市場名
	 * @param date
	 *            現在の日 (次の約定処理が始まる日。完了させる約定処理の日より1ステップ後になる)
	 * @param session
	 *            現在の節 (次の約定処理が始まる節。完了させる約定処理の節より1ステップ後になる)
	 * @throws IOException
	 */
	private void finishAuction(String auctioneerName, int date, int session) throws IOException {
		WAuctioneer au = fAuctioneers.get(auctioneerName);
		au.finishAuction();
		WBoard board = au.getBoard();
		WOrderManager om = fOrderManagers.get(auctioneerName);
		WPriceInfoDB pi = fPriceInfoDBs.get(auctioneerName);
		// 約定価格を AccountManager に登録する
		registerContractsToAccountManager(auctioneerName);
		// 市場価格を PriceInfoDB に登録する
		pi.addPriceInfo(au.getDate(), au.getSession(), au.getEarliestSlot(),
			board.getTotalPriceTable(), board.getTotalVolumeTable());
		pi.incrementPointer();
		// オーダーを Board から OrderManager へ移す
		moveOrdersFromBoard(board, om);
		cancelFailedOrders(om, auctioneerName, date, session);
		// cancelOrdersOfBankruptedMembers(om);
		log.trace(om.toString());
		// ロギング
		updateLogOfAuctioneer(auctioneerName);
		// 使い終わった板を消す
		au.flushBoard();
	}

	/**
	 * 各オーダーの約定情報を WAccountManager に登録する
	 */
	private void registerContractsToAccountManager(String auctioneerName) {
		WAuctioneer au = fAuctioneers.get(auctioneerName);
		int date = au.getDate();
		int session = au.getSession();
		WBoard board = au.getBoard();
		for (Iterator orders = board.getOrderArray(); orders.hasNext();) {
			WOrder o = (WOrder) orders.next();
			int userID = o.getUserID();
			int sellBuy = o.getSellBuy();
			Vector contracts = o.getContracts();
			for (Iterator infos = contracts.iterator(); infos.hasNext();) {
				WContract info = (WContract) infos.next();
				if (info.getDate() != date) {
					System.err
						.println("Error: info.getDate() != fDate in WMart.registerContractsToAccountManager");
					System.exit(5);
				}
				if (info.getSession() == session) {
					double price = o.getContractPrice();
					double volume = o.getContractVolume();
					if (!fAccountManager.registerContract(userID, date, session, sellBuy, price,
						volume)) {
						System.err.print("Error: Can't append the contract information for Member"
							+ userID);
						System.exit(5);
					}
				}
			}
		}
		fAccountManager.checkBankruptForAllAccounts();
		if (!fAccountManager.checkConsistency()) {
			System.err
				.print("Error: Account consistency violation in WMart.registerContractsToAccountManager");
			System.err.println("Random Seed:" + URandom.getSeed());
			System.exit(5);
		}
	}

	/**
	 * すべてのオーダーを Board から OrderManager へ移動する
	 */
	private void moveOrdersFromBoard(WBoard board, WOrderManager om) {
		om.markCurrentIndex();
		for (Iterator orders = board.getOrderArray(); orders.hasNext();) {
			WOrder o = (WOrder) orders.next();
			om.addOrder(o);
		}
		board.clear();
	}

	/**
	 * 受付期間を過ぎても約定できなかった注文をキャンセルする
	 */
	private void cancelFailedOrders(WOrderManager om, String auctioneerName, int date, int session) {
		WAuctioneer au = fAuctioneers.get(auctioneerName);
		for (Enumeration<WOrderArray> itr1 = om.getOrderArrays(); itr1.hasMoreElements();) {
			WOrderArray orderArray = itr1.nextElement();
			for (Iterator<WOrder> itr2 = orderArray.getUncontractedOrders().iterator(); itr2
				.hasNext();) {
				WOrder o = itr2.next();
				if (o.getEarliestSlot() < au.getEarliestSlotAt(date, session)) {
					itr2.remove();
				}
			}
		}
		return;
	}

	/**
	 * 破産した会員の注文を全てキャンセルする。
	 */
	private void cancelOrdersOfBankruptedMembers(WOrderManager om) {
		Enumeration accounts = fAccountManager.getBankruptedAccounts();
		while (accounts.hasMoreElements()) {
			WAccount account = (WAccount) accounts.nextElement();
			int userID = account.getUserID();
			om.cancelAllOrdersOfMember(userID);
		}
	}

	/**
	 * 現在のマスター状態を返す
	 * 
	 * @return マスター状態
	 */
	public WServerStatusMaster getStatus() {
		WServerStatusMaster result = null;
		try {
			fStateLock.readLock();
			result = (WServerStatusMaster) fStatus.clone();
		} catch (Exception ex) {
			System.err.println("Exception: " + ex + " in UMart.getMarketStatus");
			System.exit(5);
		} finally {
			fStateLock.readUnlock();
			return result;
		}
	}

	/**
	 * サーバの設定を標準出力する。
	 */
	public void printOn() {
		System.out.println("WMart is ready");
		System.out.println("No of boards per day: " + fSessionsPerDay);
	}

	/*
	 * コマンド応答 ============================================================
	 */

	/**
	 * Loginコマンドへの応答を処理する。
	 * 
	 * @param userName
	 *            ユーザー名
	 * @param passwd
	 *            パスワード
	 * @return 対応するログイン状態(ULoginStatus)
	 */
	public ULoginStatus doLogin(String loginName, String passwd) {
		int userID = fAccountManager.getUserID(loginName, passwd);
		if (userID < 0) {
			return null;
		}
		return fLoginManager.findLoginStatus(userID);
	}

	/**
	 * Balancesコマンドへの応答を処理する。
	 * 
	 * @param todayHash
	 *            当日分の残高照会情報
	 * @param yesterdayHash
	 *            前日分の残高照会情報
	 * @param userID
	 *            ユーザID
	 */
	public WCommandStatus doBalances(HashMap todayHash, HashMap yesterdayHash, int userID) {
		WCommandStatus ucs = new WCommandStatus();
		try {
			fStateLock.readLock();
			if (!fCmdExecutableChecker.isExecutable(WCBalancesCore.CMD_NAME, fStatus, userID)) {
				ucs.setStatus(false);
				ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
				ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
				return ucs;
			}
			ucs.setStatus(true);
			WAccount account = fAccountManager.getAccount(userID);
			if (account == null) {
				System.err.println("Account not found in WMart.doBalances: UserID=" + userID);
				throw new Exception();
			}
			setBalanceToHashMap(todayHash, account.getTodayBalance());
			setBalanceToHashMap(yesterdayHash, account.getYesterdayBalance());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Exception: " + e + " in WMart.doBalances");
			System.exit(5);
		} finally {
			fStateLock.readUnlock();
			return ucs;
		}
	}

	/**
	 * Executionsコマンドへの応答を処理する。
	 * 
	 * @param arrayList
	 *            ハッシュマップを格納するためのリスト
	 * @param userID
	 *            ユーザID
	 * @param auctioneerName
	 *            市場名
	 */
	public WCommandStatus doExecutions(ArrayList arrayList, int userID, String auctioneerName) {
		WCommandStatus ucs = new WCommandStatus();
		try {
			fStateLock.readLock();
			if (!fCmdExecutableChecker.isExecutable(WCExecutionsCore.CMD_NAME, fStatus, userID)) {
				ucs.setStatus(false);
				ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
				ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
				return ucs;
			}
			ucs.setStatus(true);
			WOrderManager om = fOrderManagers.get(auctioneerName);
			synchronized (om) {
				Vector orderArray = om.getAllOrders(userID);
				for (Iterator<WOrder> orders = orderArray.iterator(); orders.hasNext();) {
					WOrder o = orders.next();
					for (Iterator<WContract> infos = o.getContracts().iterator(); infos.hasNext();) {
						WContract info = infos.next();
						HashMap hash = new HashMap();
						setContractInfoToHash(hash, o, info);
						arrayList.add(hash);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Exception: " + e + " in WMart.doExecutions");
			System.exit(5);
		} finally {
			fStateLock.readUnlock();
			return ucs;
		}
	}

	/**
	 * MarketPriceコマンドへの応答を処理する。
	 * 
	 * @param arrayList
	 *            ハッシュマップを格納するためのリスト
	 * @param brandName
	 *            銘柄名
	 * @param auctioneerName
	 *            市場名
	 * @param noOfSteps
	 *            過去何ステップ分の価格が必要か?
	 */
	public WCommandStatus doMarketPrice(ArrayList arrayList, String brandName,
		String auctioneerName, int noOfSteps) {
		WCommandStatus ucs = new WCommandStatus();
		try {
			fStateLock.readLock();
			if (!fCmdExecutableChecker.isExecutable(WCMarketPriceCore.CMD_NAME, fStatus, SU_ID)) {
				ucs.setStatus(false);
				ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
				ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
				return ucs;
			}
			ucs.setStatus(true);
			String[] prices = fPriceInfoDBs.get(auctioneerName).getPrices(noOfSteps);
			int date = fStatus.getDate();
			int boardNo = fStatus.getSession();
			int steps = (date - 1) * fSessionsPerDay + boardNo;
			for (int i = 0; i < prices.length; ++i) {
				HashMap elem = new HashMap();
				elem.put(WCMarketPriceCore.STRING_BRAND_NAME, brandName);
				if (steps > 0) {
					date = (steps - 1) / fSessionsPerDay + 1;
					boardNo = steps - (date - 1) * fSessionsPerDay;
				} else {
					date = -(Math.abs(steps) / fSessionsPerDay);
					boardNo = fSessionsPerDay + steps - (date * fSessionsPerDay);
				}
				elem.put(WCMarketPriceCore.INT_DAY, new Integer(date));
				elem.put(WCMarketPriceCore.INT_BOARD_NO, new Integer(boardNo));
				elem.put(WCMarketPriceCore.INT_STEP, new Integer(steps));
				elem.put(WCMarketPriceCore.STRING_MARKET_PRICE, new String(prices[i]));
				arrayList.add(elem);
				--steps;
			}
		} catch (Exception e) {
			System.err.println("Exception: " + e + " in WMart.doMarketPrice");
			System.exit(5);
		} finally {
			fStateLock.readUnlock();
			return ucs;
		}
	}

	/**
	 * MarketStatusコマンドへの応答を処理する。取引所ワーカーの状態を返す。
	 * 
	 * @param hash
	 *            各情報を格納するためのハッシュマップ
	 * @param auctioneerName
	 *            市場名
	 */
	public WCommandStatus doMarketStatus(HashMap hash, String auctioneerName) {
		WCommandStatus ucs = new WCommandStatus();
		WAuctioneer au = fAuctioneers.get(auctioneerName);
		try {
			au.getStateLock().readLock();
			ucs.setStatus(true);
			hash.put(WCMarketStatusCore.INT_MARKET_STATUS, new Integer(au.getStatus().getState()));
		} catch (Exception e) {
			System.err.println("Exception: " + e + " in WMart.doMarketStatus");
			System.exit(5);
		} finally {
			au.getStateLock().readUnlock();
			return ucs;
		}
	}

	/**
	 * MemberProfileコマンドへの応答を処理する。
	 * 
	 * @param data
	 *            targetUserIDで指定されたユーザーの情報を格納するためのHashMap
	 * @param userID
	 *            このコマンドを実行したユーザーのユーザーID
	 * @param targetUserID
	 *            調べたいユーザーのユーザーID（-1の場合，userIDが代入される）
	 * @return 実行状態
	 */
	public WCommandStatus doMemberProfile(HashMap data, int userID, int targetUserId) {
		WCommandStatus ucs = new WCommandStatus();
		try {
			fStateLock.readLock();
			if (!fCmdExecutableChecker.isExecutable(WCMemberProfileCore.CMD_NAME, fStatus, userID)) {
				ucs.setStatus(false);
				ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
				ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
				return ucs;
			}
			if (targetUserId < 0) {
				targetUserId = userID;
			}
			WAccount account = fAccountManager.getAccount(targetUserId);
			ULoginStatus office = fLoginManager.findLoginStatus(targetUserId);
			if (account == null || office == null) {
				ucs.setStatus(false);
				ucs.setErrorCode(ICommand.INVALID_ARGUMENTS);
				ucs.setErrorMessage("TARGET_USER_ID(=" + targetUserId + ") IS OUT OF RANGE.");
				return ucs;
			}
			data.put(WCMemberProfileCore.STRING_LOGIN_NAME, account.getUserName()); // 会員名
			data.put(WCMemberProfileCore.STRING_PASSWORD, account.getPasswd()); // パスワード
			data.put(WCMemberProfileCore.STRING_ATTRIBUTE, account.getAttribute()); // エージェント属性(Human
			// or Machine)
			data.put(WCMemberProfileCore.STRING_CONNECTION, account.getConnection()); // コネクション(Remote
			// or Local)
			data.put(WCMemberProfileCore.ARRAY_LIST_ACCESS, account.getAccess()); // アクセス制限
			data.put(WCMemberProfileCore.STRING_REAL_NAME, account.getRealName()); // 実際の名前
			data.put(WCMemberProfileCore.ARRAY_LIST_SYSTEM_PARAMETERS,
				account.getSystemParameters()); // システムパラメータ
			data.put(WCMemberProfileCore.INT_SEED, new Integer(account.getSeed())); // 乱数の種
			data.put(WCMemberProfileCore.LONG_INITIAL_CASH, new Long(account.getInitialCash())); // 初期資産
			data.put(WCMemberProfileCore.LONG_TRADING_UNIT, new Long(account.getTradingUnit())); // 取引単位
			data.put(WCMemberProfileCore.LONG_FEE_PER_UNIT, new Long(account.getFeePerUnit())); // 単位取引あたりの手数料
			data.put(WCMemberProfileCore.LONG_MARGIN_RATE, new Long(account.getMarginRate())); // 証拠金率
			data.put(WCMemberProfileCore.LONG_MAX_LOAN, new Long(account.getMaxLoan())); // 借り入れ限度額
			data.put(WCMemberProfileCore.DOUBLE_INTEREST, new Double(account.getInterestPerYear())); // 借り入れ金利
			data.put(WCMemberProfileCore.INT_STATUS, new Integer(account.getStatus())); // 取引可能(+1),
			// 取引不可能(-1)
			data.put(WCMemberProfileCore.INT_NO_OF_LOGIN_AGENTS,
				new Integer(office.getNoOfLoginAgents())); // ログイン中のエージェント数
			HashMap yesterdayBalanceHash = new HashMap();
			WBalance yesterdayBalancel = account.getYesterdayBalance();
			setBalanceToHashMapForMemberProfile(yesterdayBalanceHash, yesterdayBalancel);
			data.put(WCMemberProfileCore.HASHMAP_YESTERDAY_BALANCE, yesterdayBalanceHash); // 前日収支
			HashMap todayBalanceHash = new HashMap();
			WBalance todayBalance = account.getTodayBalance();
			setBalanceToHashMapForMemberProfile(todayBalanceHash, todayBalance);
			data.put(WCMemberProfileCore.HASHMAP_TODAY_BALANCE, todayBalanceHash); // 当日収支
			HashMap positionHash = new HashMap();
			UPosition pos = account.getPosition();
			setPositionToHashMapForMemberProfile(positionHash, pos);
			data.put(WCMemberProfileCore.HASHMAP_POSITION, positionHash); // ポジション
			ucs.setStatus(true);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(5);
		} finally {
			fStateLock.readUnlock();
			return ucs;
		}
	}

	/**
	 * OrderRequestコマンドへの応答を処理する。
	 * 
	 * @param hash
	 *            各情報を格納するためのハッシュマップ
	 * @param userID
	 *            ユーザID
	 * @param brandName
	 *            商品名 (不使用)
	 * @param auctioneerName
	 *            市場名
	 * @param sellBuy
	 *            売買区分
	 * @param marketLimit
	 *            成行指値区分
	 * @param price
	 *            希望取引価格
	 * @param spec
	 *            注文明細 "商品名:希望数量:最早時刻:最遅時刻:延べ時間;" の繰り返し
	 */
	public WCommandStatus doOrderRequest(HashMap hash, int userID, String brandName,
		String auctioneerName, int sellBuy, int marketLimit, long price, String spec) {
		WCommandStatus ucs = new WCommandStatus();
		try {
			fStateLock.readLock();
			if (!fCmdExecutableChecker.isExecutable(WCOrderRequestCore.CMD_NAME, fStatus, userID)) {
				ucs.setStatus(false);
				ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
				ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
				return ucs;
			}
			WAccount account = fAccountManager.getAccount(userID);
			if (account.getStatus() == WAccount.UNAVAILABLE) {
				ucs.setStatus(false);
				ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
				ucs.setErrorMessage("YOU ARE BANKRUPTED!!");
				return ucs;
			}
			String userName = account.getUserName();
			int date = fStatus.getDate();
			int session = fStatus.getSession();
			WOrderManager om = fOrderManagers.get(auctioneerName);
			WOrder o = om.createOrder(userID, userName, brandName, auctioneerName, sellBuy,
				marketLimit, price, spec, date, session);
			if (o == null) {
				ucs.setStatus(false);
				ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
				ucs.setErrorMessage("INVALID ORDER! A selling order cannot have multiple goods.");
				return ucs;
			}
			WAuctioneer au = fAuctioneers.get(auctioneerName);
			if (o.getEarliestSlot() < au.getEarliestSlotAt(date, session)
				|| o.getLatestSlot() > au.getLatestSlotAt(date, session)) {
				ucs.setStatus(false);
				ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
				ucs.setErrorMessage("INVALID ORDER! Slot must be between "
					+ au.getEarliestSlotAt(date, session) + " and "
					+ au.getLatestSlotAt(date, session) + " whereas the orderSpec is ["
					+ o.getOrderSpec() + "]");
				return ucs;
			}
			ucs.setStatus(true);
			synchronized (om) {
				om.addOrder(o);
				om.registerOrderToHistory(o);
				fOrderCommandLog.registerOrderRequest(o);
			}
			hash.put(WCOrderRequestCore.LONG_ORDER_ID, Long.toString(o.getOrderID()));
			hash.put(WCOrderRequestCore.STRING_ORDER_TIME, extractTime(o.getTime()));
		} catch (Exception e) {
			System.err.println("Exception: " + e + " in WMart.doOrderRequest");
			e.printStackTrace();
			System.exit(5);
		} finally {
			fStateLock.readUnlock();
			return ucs;
		}
	}

	/**
	 * OrderStatusコマンドへの応答を処理する。
	 * 
	 * @param arrayList
	 *            ハッシュマップを格納するためのリスト
	 * @param userID
	 *            ユーザID
	 * @param auctioneerName
	 *            市場名
	 */
	public WCommandStatus doOrderStatus(ArrayList arrayList, int userID, String auctioneerName) {
		WCommandStatus ucs = new WCommandStatus();
		try {
			fStateLock.readLock();
			if (!fCmdExecutableChecker.isExecutable(WCOrderStatusCore.CMD_NAME, fStatus, userID)) {
				ucs.setStatus(false);
				ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
				ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
				return ucs;
			}
			ucs.setStatus(true);
			WOrderManager om = fOrderManagers.get(auctioneerName);
			synchronized (om) {
				WOrderArray orders = om.getOrderArray(userID);
				if (orders == null) {
					System.err.print("OrderArray not found in WMart.doOrderStatus");
					System.exit(-5);
				}
				// 約定済み注文は直近セッションで成立した分のみ載せる
				for (int i = orders.getIndexOfLatestContractedOrder(); i < orders
					.getNoOfContractedOrders(); i++) {
					WOrder o = orders.getContractedOrderAt(i);
					HashMap hash = new HashMap();
					setOrderMessage(hash, o);
					arrayList.add(hash);
				}
				// 未約定注文は全部載せる
				for (int i = 0; i < orders.getNoOfUncontractedOrders(); i++) {
					WOrder o = orders.getUncontractedOrderAt(i);
					HashMap hash = new HashMap();
					setOrderMessage(hash, o);
					arrayList.add(hash);
				}
			}
		} catch (Exception e) {
			System.err.println("Exception: " + e + " in WMart.doOrderStatus");
			System.exit(5);
		} finally {
			fStateLock.readUnlock();
			return ucs;
		}
	}

	/**
	 * Positionコマンドへの応答を処理する。
	 * 
	 * @param hash
	 *            各情報を格納するためのハッシュマップ
	 * @param userID
	 *            ユーザID
	 */
	public WCommandStatus doPosition(HashMap hash, int userID) {
		WCommandStatus ucs = new WCommandStatus();
		try {
			fStateLock.readLock();
			if (!fCmdExecutableChecker.isExecutable(WCPositionCore.CMD_NAME, fStatus, userID)) {
				ucs.setStatus(false);
				ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
				ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
				return ucs;
			}
			ucs.setStatus(true);
			WAccount account = fAccountManager.getAccount(userID);
			if (account == null) {
				System.err.println("Account not found in UMart.doPosition");
				System.exit(-5);
			}
			UPosition pos = account.getPosition();
			long todaySell = 0;
			long todayBuy = 0;
			for (int boardNo = 1; boardNo <= fStatus.getSession(); ++boardNo) {
				todaySell += pos.getTodaySellPosition(boardNo);
				todayBuy += pos.getTodayBuyPosition(boardNo);
			}
			hash.put(WCPositionCore.LONG_TODAY_SELL, new Long(todaySell));
			hash.put(WCPositionCore.LONG_TODAY_BUY, new Long(todayBuy));
			long yesterdaySell = pos.getSumOfSellPositionUntilYesterday();
			long yesterdayBuy = pos.getSumOfBuyPositionUntilYesterday();
			hash.put(WCPositionCore.LONG_YESTERDAY_SELL, new Long(yesterdaySell));
			hash.put(WCPositionCore.LONG_YESTERDAY_BUY, new Long(yesterdayBuy));
		} catch (Exception e) {
			System.err.println("Exception: " + e + " in WMart.doPosition");
			System.exit(5);
		} finally {
			fStateLock.readUnlock();
			return ucs;
		}
	}

	/**
	 * ServerDateコマンドへの応答を処理する。
	 * 
	 * @param hash
	 *            各情報を格納するためのハッシュマップ
	 */
	public WCommandStatus doServerDate(HashMap hash) {
		WCommandStatus ucs = new WCommandStatus();
		try {
			fStateLock.readLock();
			int date = fStatus.getDate();
			int boardNo = fStatus.getSession();
			ucs.setStatus(true);
			hash.put(WCServerDateCore.INT_DAY, new Integer(date));
			hash.put(WCServerDateCore.INT_BOARD_NO, new Integer(boardNo));
		} catch (Exception e) {
			System.err.println("Exception: " + e + " in WMart.doServerDate");
			System.exit(5);
		} finally {
			fStateLock.readUnlock();
			return ucs;
		}
	}

	/**
	 * ServerStatusコマンドを処理する。取引所マスターの状態を返す。
	 * 
	 * @param data
	 *            サーバー状態を格納するためのHashMap
	 * @param userID
	 *            このコマンドを実行したユーザーのユーザーID
	 * @return 実行状態
	 */
	public WCommandStatus doServerStatus(HashMap data, int userID) {
		WCommandStatus ucs = new WCommandStatus();
		try {
			fStateLock.readLock();
			if (!fCmdExecutableChecker.isExecutable(WCServerStatusCore.CMD_NAME, fStatus, userID)) {
				ucs.setStatus(false);
				ucs.setErrorCode(ICommand.UNACCEPTABLE_COMMAND);
				ucs.setErrorMessage(fCmdExecutableChecker.getErrorMessage());
				return ucs;
			}
			int date = fStatus.getDate();
			data.put(WCServerStatusCore.INT_DATE, new Integer(date));
			int boardNo = fStatus.getSession();
			data.put(WCServerStatusCore.INT_BOARD_NO, new Integer(boardNo));
			int state = fStatus.getState();
			data.put(WCServerStatusCore.INT_STATE, new Integer(state));
			ucs.setStatus(true);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(5);
		} finally {
			fStateLock.readUnlock();
			return ucs;
		}
	}

	/**
	 * ServerTimeコマンドへの応答を処理する。
	 * 
	 * @param hash
	 *            各情報を格納するためのハッシュマップ
	 */
	public WCommandStatus doServerTime(HashMap hash) {
		WCommandStatus ucs = new WCommandStatus();
		ucs.setStatus(true);
		hash.put(WCServerTimeCore.STRING_SERVER_TIME, extractTime(new Date()));
		return ucs;
	}

	/*
	 * コマンド応答の補助 ============================================================
	 */

	/**
	 * 収支情報を hash に格納する。doBalances から呼ばれる。
	 * 
	 * @param hash
	 *            各情報を格納するためのハッシュマップ
	 * @param balance
	 *            収支情報
	 */
	private void setBalanceToHashMap(HashMap hash, WBalance balance) {
		balance.updateCash();
		hash.put(WCBalancesCore.LONG_CASH, new Long(balance.getCash() + balance.getMargin()));
		hash.put(WCBalancesCore.LONG_MARGIN, new Long(balance.getMargin()));
		hash.put(WCBalancesCore.LONG_UNREALIZED_PROFIT, new Long(balance.getUnrealizedProfit()));
		hash.put(WCBalancesCore.LONG_SETTLED_PROFIT, new Long(balance.getProfit()));
		hash.put(WCBalancesCore.LONG_FEE, new Long(balance.getSumOfFee()));
		hash.put(WCBalancesCore.LONG_INTEREST, new Long(balance.getSumOfInterest()));
		hash.put(WCBalancesCore.LONG_LOAN, new Long(balance.getLoan()));
		hash.put(WCBalancesCore.LONG_SURPLUS, new Long(balance.getCash()));
	}

	/**
	 * オーダー o の約定情報 info を hash に格納する。doExecutions から呼ばれる。
	 * 
	 * @param hash
	 *            各情報を格納するためのハッシュマップ
	 * @param o
	 *            オーダー
	 * @param info
	 *            約定情報
	 */
	private void setContractInfoToHash(HashMap hash, WOrder o, WContract info) {
		hash.put(WCExecutionsCore.LONG_CONTRACT_ID, new Long(info.getContractID()));
		hash.put(WCExecutionsCore.STRING_CONTRACT_TIME, extractTime(info.getTime()));
		hash.put(WCExecutionsCore.LONG_ORDER_ID, new Long(o.getOrderID()));
		hash.put(WCExecutionsCore.STRING_BRAND_NAME, new String(o.getBrandName()));
		hash.put(WCExecutionsCore.STRING_AUCTIONEER_NAME, new String(o.getAuctioneerName()));
		hash.put(WCExecutionsCore.INT_SELL_BUY, new Integer(o.getSellBuy()));
		hash.put(WCExecutionsCore.DOUBLE_CONTRACT_PRICE, new Double(info.getPrice()));
		hash.put(WCExecutionsCore.DOUBLE_CONTRACT_VOLUME, new Double(info.getVolume()));
		hash.put(WCExecutionsCore.STRING_SOLD_PRICES, new String(info.encodeSoldPrices()));
		hash.put(WCExecutionsCore.STRING_SOLD_VOLUMES, new String(info.encodeSoldVolumes()));
	}

	/**
	 * 一般ユーザーの収支情報を hash に格納する．doMemberProfileから 呼ばれる．
	 * 
	 * @param hash
	 *            一般ユーザーの収支情報を格納するためのHashMap
	 * @param bal
	 *            一般ユーザーの収支情報
	 */
	private void setBalanceToHashMapForMemberProfile(HashMap hash, WBalance bal) {
		long initialCash = bal.getInitialCash();
		hash.put(WCMemberProfileCore.LONG_INITIAL_CASH, new Long(initialCash)); // 初期所持金
		long loan = bal.getLoan();
		hash.put(WCMemberProfileCore.LONG_LOAN, new Long(loan)); // 借入金
		long unrealizedProfit = bal.getUnrealizedProfit();
		hash.put(WCMemberProfileCore.LONG_UNREALIZED_PROFIT, new Long(unrealizedProfit)); // 未実現損益
		long margin = bal.getMargin();
		hash.put(WCMemberProfileCore.LONG_MARGIN, new Long(margin)); // 預託証拠金
		long sumOfFee = bal.getSumOfFee();
		hash.put(WCMemberProfileCore.LONG_SUM_OF_FEE, new Long(sumOfFee)); // 総支払い手数料
		long sumOfInterest = bal.getSumOfInterest();
		hash.put(WCMemberProfileCore.LONG_SUM_OF_INTEREST, new Long(sumOfInterest)); // 総支払い金利
		long cash = bal.getCash();
		hash.put(WCMemberProfileCore.LONG_CASH, new Long(cash)); // 保有現金
		long profit = bal.getProfit();
		hash.put(WCMemberProfileCore.LONG_PROFIT, new Long(profit)); // 実現損益
	}

	/**
	 * 一般ユーザーのポジション情報を hash に格納する．doMemberProfile から呼ばれる．
	 * 
	 * @param hash
	 *            一般ユーザーのポジション情報を格納するためのHashMap
	 * @param pos
	 *            一般ユーザーのポジション情報
	 */
	private void setPositionToHashMapForMemberProfile(HashMap hash, UPosition pos) {
		long sumOfSellPositionsUntilYesterday = pos.getSumOfSellPositionUntilYesterday();
		hash.put(WCMemberProfileCore.LONG_SUM_OF_SELL_POSITIONS_UNTIL_YESTERDAY, new Long(
			sumOfSellPositionsUntilYesterday)); // 前日までの売ポジションの合計
		long sumOfBuyPositionsUntilYesterday = pos.getSumOfBuyPositionUntilYesterday();
		hash.put(WCMemberProfileCore.LONG_SUM_OF_BUY_POSITIONS_UNTIL_YESTERDAY, new Long(
			sumOfBuyPositionsUntilYesterday)); // 前日までの買ポジションの合計
		long todaySellPositions = pos.getSumOfTodaySellPosition();
		hash.put(WCMemberProfileCore.LONG_TODAY_SELL_POSITIONS, new Long(todaySellPositions)); // 当日の売ポジションの合計
		long todayBuyPositions = pos.getSumOfTodayBuyPosition();
		hash.put(WCMemberProfileCore.LONG_TODAY_BUY_POSITIONS, new Long(todayBuyPositions)); // 当日の買ポジションの合計
	}

	/**
	 * オーダー o の各データを hash に格納する。doOrderStatus から呼ばれる。
	 * 
	 * @param hash
	 *            情報を格納するためのハッシュマップ
	 * @param o
	 *            注文
	 */
	private void setOrderMessage(HashMap hash, WOrder o) {
		hash.put(WCOrderStatusCore.LONG_ORDER_ID, Long.toString(o.getOrderID()));
		hash.put(WCOrderStatusCore.STRING_ORDER_TIME, extractTime(o.getTime()));
		hash.put(WCOrderStatusCore.STRING_BRAND_NAME, o.getBrandName());
		hash.put(WCOrderStatusCore.STRING_AUCTIONEER_NAME, o.getAuctioneerName());
		hash.put(WCOrderStatusCore.INT_SELL_BUY, Integer.toString(o.getSellBuy()));
		hash.put(WCOrderStatusCore.INT_MARKET_LIMIT, Integer.toString(o.getMarketLimit()));
		hash.put(WCOrderStatusCore.LONG_ORDER_PRICE, Long.toString(o.getOrderPrice()));
		hash.put(WCOrderStatusCore.STRING_ORDER_SPEC, o.getOrderSpec());
		hash.put(WCOrderStatusCore.DOUBLE_CONTRACT_PRICE, Double.toString(o.getContractPrice()));
		hash.put(WCOrderStatusCore.STRING_CONTRACT_SPEC, o.getSoldSpec());
	}

	/**
	 * dateを"hh:mm:ss"形式に変換する。
	 * 
	 * @param date
	 *            実時間
	 */
	private String extractTime(Date date) {
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}

	/*
	 * ロギング ============================================================
	 */

	/**
	 * ログを更新する．
	 * 
	 * @param date
	 *            現在の日付
	 * @param session
	 *            現在の節
	 * @param state
	 *            マーケット状態
	 */
	private void updateLog(int date, int session, int state) throws IOException {
		if (fLogFlag == WMart.NO_LOG) {
			return;
		}
		switch (state) {
		case WServerStatusMaster.CONCLUDING_AUCTIONS: // 前半終了後
		case WServerStatusMaster.PREPARING_AUCTIONS: // 後半終了後
			if (fLogFlag == WMart.DETAILED_LOG) {
				writeOrderCommandLog(date, session);
				writeAccountLog(date, session);
			}
			fOrderCommandLog.clear();
			break;
		case WServerStatusMaster.AFTER_TRADING: // 全取引終了後
			if (fLogFlag == WMart.DETAILED_LOG) {
				writeAccountLog(date, session);
			}
			break;
		default:
			System.err.println("Invalid state " + state + " in WMart.updateLog()");
		}
	}

	/**
	 * 注文/取消コマンドの履歴をファイルに書き出す
	 */
	private void writeOrderCommandLog(int date, int session) throws IOException {
		String filepath = String.format(path("%s/%s/order%03d%02d.csv"), fLogDir,
			WMart.ORDER_LOG_DIR, date, session);
		PrintWriter pw = new PrintWriter(new FileOutputStream(filepath));
		fOrderCommandLog.writeTo(pw);
		pw.close();
	}

	/**
	 * 口座の履歴をファイルに書き出す
	 */
	private void writeAccountLog(int date, int session) throws IOException {
		String filepath = String.format(path("%s/%s/account%03d%02d.csv"), fLogDir,
			WMart.ACCOUNT_LOG_DIR, date, session);
		PrintWriter pw = new PrintWriter(new FileOutputStream(filepath));
		WAccountLog log = new WAccountLog(date, session, fAccountManager);
		log.writeTo(pw);
		pw.close();
	}

	/**
	 * 各市場ごとのログを更新する
	 * 
	 * @param auctioneerName
	 */
	private void updateLogOfAuctioneer(String auctioneerName) throws IOException {
		if (fLogFlag == WMart.NO_LOG) {
			return;
		}
		WAuctioneer au = fAuctioneers.get(auctioneerName);
		int date = au.getDate();
		int session = au.getSession();
		writePriceInfo(date, session, auctioneerName);
		if (fLogFlag == WMart.DETAILED_LOG) {
			writeExecutionLog(date, session, auctioneerName);
		}
	}

	/**
	 * 価格系列をファイルに書き出す
	 */
	private void writePriceInfo(int date, int session, String auctioneerName) throws IOException {
		String filepath = String.format(path("%s/price_%s.csv"), fLogDir, auctioneerName);
		PrintWriter pw = new PrintWriter(new FileOutputStream(filepath, true));
		WPriceInfoDB pi = fPriceInfoDBs.get(auctioneerName);
		pi.writeLatestPriceInfo(pw);
		pw.close();
	}

	/**
	 * 約定履歴をファイルに書き出す
	 */
	private void writeExecutionLog(int date, int session, String auctioneerName) throws IOException {
		String filepath = String.format(path("%s/%s/execution%03d%02d%s.csv"), fLogDir,
			WMart.EXECUTION_LOG_DIR, date, session, auctioneerName);
		PrintWriter pw = new PrintWriter(new FileOutputStream(filepath));
		WExecutionLog log = new WExecutionLog(date, session, fOrderManagers.get(auctioneerName));
		log.writeTo(pw);
		pw.close();
	}

	/**
	 * MemberLogをファイルに書き出す．(初期化時に呼ばれる)
	 * 
	 * @param filepath
	 *            String 出力ファイル名
	 * @throws IOException
	 */
	private void writeMemberLog(String filepath) throws IOException {
		PrintWriter pw = new PrintWriter(new FileOutputStream(filepath));
		fMemberLog.writeTo(pw);
		pw.close();
	}

	/**
	 * パス区切り文字を環境に合わせる
	 */
	private String path(String slashSeparatedPath) {
		return slashSeparatedPath.replace('/', File.separatorChar);
	}

	/*
	 * 初期化 ============================================================
	 */

	/**
	 * ログの初期化を行う．
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void initLog() throws FileNotFoundException, IOException {
		initLog(null, false);
	}

	/**
	 * ログの初期化を行う．
	 * 
	 * @param params
	 *            "logdir=(path)" を含むシステムパラメータ (コロン区切り文字列)。<br />
	 *            "logdir=(path)" を含まない場合、出力先ディレクトリ名は YYMMDD-hhmmss となる
	 * @param isSimple
	 *            true:簡易ログ false:詳細ログ
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void initLog(String params, boolean isSimple) throws FileNotFoundException, IOException {
		fLogDir = extractParameter(params, "logdir");
		if (fLogDir == null) {
			fLogDir = makeDefaultDirectoryName();
		}
		fLogFlag = (isSimple) ? WMart.SIMPLE_LOG : WMart.DETAILED_LOG;
		if (!makeDirectory(fLogDir)) {
			throw new IOException();
		}
		if (fLogFlag == WMart.DETAILED_LOG) {
			if (!makeDirectory(fLogDir + File.separator + WMart.ORDER_LOG_DIR)) {
				throw new IOException();
			}
			if (!makeDirectory(fLogDir + File.separator + WMart.EXECUTION_LOG_DIR)) {
				throw new IOException();
			}
			if (!makeDirectory(fLogDir + File.separator + WMart.ACCOUNT_LOG_DIR)) {
				throw new IOException();
			}
			if (!makeDirectory(fLogDir + File.separator + WMart.RESOURCE_LOG_DIR)) {
				throw new IOException();
			}
		}
		writeMemberLog(fLogDir + File.separator + "Member.csv");
	}

	/**
	 * ログ出力先ディレクトリ名を取得
	 * 
	 * @return ログ出力先ディレクトリ名
	 */
	public String getLogDir() {
		return fLogDir;
	}

	/**
	 * システムパラメータを取得する
	 * 
	 * @param paramStr
	 *            システムパラメータ
	 */
	private String extractParameter(String paramStr, String name) {
		String[] params = paramStr.split(":");
		for (int i = 0; i < params.length; i++) {
			StringTokenizer st = new StringTokenizer(params[i], "= ");
			String k = st.nextToken();
			String v = st.nextToken();
			if (k.equalsIgnoreCase(name)) {
				return v;
			}
		}
		return null;
	}

	/**
	 * YYMMDD-hhmmss を返す
	 * 
	 * @return String "YYMMDD-hhmmss"
	 */
	private String makeDefaultDirectoryName() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR) % 100;
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		return String.format("%02d%02d%02d-%02d%02d%02d", year, month, day, hour, minute, second);
	}

	/**
	 * 新しいディレクトリを作成する。同名のディレクトリがあったら先に中身ごと削除する。
	 * 
	 * @param name
	 *            ディレクトリ名
	 * @return
	 */
	private boolean makeDirectory(String name) {
		File target = new File(name);
		if (target.exists()) {
			if (!name.isEmpty() && !name.startsWith(".")) {
				if (!deleteFile(target)) {
					return false;
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					return false;
				}
				target = new File(name);
			}
		}
		return target.mkdir();
	}

	/**
	 * ファイル／ディレクトリを再帰的に削除する
	 * 
	 * @param name
	 * @return
	 */
	private boolean deleteFile(File target) {
		if (!target.exists()) {
			return false;
		} else if (target.isFile()) {
			return target.delete();
		} else if (target.isDirectory()) {
			File[] contents = target.listFiles();
			for (int i = 0; i < contents.length; i++) {
				File content = contents[i];
				if (content.isDirectory()) {
					if (!deleteFile(content)) {
						return false;
					}
				} else {
					if (!content.delete()) {
						return false;
					}
				}
			}
			return target.delete();
		}
		return false;
	}

	/**
	 * fAuctioneers の初期設定を行う
	 */
	private void setupAuctioneers() {
		for (Iterator<Entry<String, WAuctioneer>> itr = fAuctioneers.entrySet().iterator(); itr
			.hasNext();) {
			Entry<String, WAuctioneer> entry = itr.next();
			String name = entry.getKey();
			WAuctioneer au = entry.getValue();
			au.initialize(name);
		}
	}

	/**
	 * fPriceInfoDBs の初期設定を行う
	 */
	private void setupPriceInfoDBs() {
		for (Iterator<Entry<String, WPriceInfoDB>> itr = fPriceInfoDBs.entrySet().iterator(); itr
			.hasNext();) {
			Entry<String, WPriceInfoDB> entry = itr.next();
			String name = entry.getKey();
			WPriceInfoDB db = entry.getValue();
			db.initialize(name);
		}
	}

	/**
	 * fOrderManagers の初期設定を行う。 事前に fAccountManager を初期化すること。
	 */
	private void setupOrderManagers() {
		for (Iterator<Entry<String, WOrderManager>> itr = fOrderManagers.entrySet().iterator(); itr
			.hasNext();) {
			Entry<String, WOrderManager> entry = itr.next();
			String name = entry.getKey();
			WOrderManager om = entry.getValue();
			om.setName(name);
			Enumeration accounts = fAccountManager.getAccounts();
			while (accounts.hasMoreElements()) {
				WAccount account = (WAccount) accounts.nextElement();
				om.createOrderArray(account.getUserID());
			}
		}
	}

	/**
	 * ネットワーク環境用またはスタンドアロン環境用のログインマネージャを生成する．
	 * 
	 * @return ログインマネージャ
	 */
	private ULoginManager createLoginManager() {
		return new ULoginManager();
	}

	/**
	 * fLoginManger の初期設定を行う。 事前に fAccountManager を初期化すること。
	 */
	private void setupLoginManager() {
		fLoginManager.creatLoginStatus(WMart.SU_ID);
		Enumeration accounts = fAccountManager.getAccounts();
		while (accounts.hasMoreElements()) {
			WAccount account = (WAccount) accounts.nextElement();
			fLoginManager.creatLoginStatus(account.getUserID());
		}
	}

	/**
	 * fCmdExecutableChecker の初期設定を行う。
	 */
	private void setupCmdExecutableChecker(String rsc) {
		try {
			URL dataURL = getClass().getResource(rsc);
			BufferedReader br = new BufferedReader(new InputStreamReader(dataURL.openStream()));
			fCmdExecutableChecker.readFrom(br);
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(5);
		}
	}

	/**
	 * ローカルホストで動作するエージェントを登録する．
	 * 
	 * @param agent
	 *            WBaseStrategyを継承したエージェント
	 */
	public boolean appendStrategy(WBaseAgent agent) {
		String name = agent.getLoginName();
		String passwd = agent.getPasswd();
		ULoginStatus loginStatus = doLogin(name, passwd);
		if (loginStatus == null) {
			System.err.println("Can't find the office with (" + name + "," + passwd + ")");
			return false;
		}
		if (loginStatus.getNoOfLoginAgents() > 0) {
			System.err.println("The agent has already logged in!!");
			return false;
		}
		int id = loginStatus.getUserID();
		WProtocolForLocalClient umcp = new WProtocolForLocalClient();
		umcp.setConnection(this, id);
		agent.setCProtocol(umcp);
		agent.setUserID(id);
		fStrategyArray.add(agent);
		loginStatus.incrementNoOfLoginAgents();
		return true;
	}

}