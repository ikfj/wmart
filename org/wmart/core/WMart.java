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
 * ������}�X�^�[�N���X (�،���Б���)
 * 
 * @author Ikki Fujiwara, NII
 */
@SuppressWarnings("finally")
public class WMart {

	/** �X�[�p�[���[�U�[��ID */
	public static int SU_ID;
	/** �T�[�o�[�^�p���� */
	private int fMaxDate;
	/** 1��������̔񂹉� */
	private int fSessionsPerDay;
	/** ��������ΏۃX���b�g�� */
	private int fSlotsSpot;
	/** �敨����ΏۃX���b�g�� */
	private int fSlotsForward;
	/** ���[�N�t���[�̍ő咷�� */
	private int fMaxLength;

	/** ������ */
	private UMemberList fMemberLog;
	/** �G�[�W�F���g�̔z�� */
	private ArrayList fStrategyArray;
	/** �N���C�A���g�̃��O�C���Ǘ� */
	private ULoginManager fLoginManager;
	/** �����Ǘ� */
	private WAccountManager fAccountManager;
	/** �R�}���h���� */
	private WOrderCommandLog fOrderCommandLog;

	/** ���[�J�[ */
	private TreeMap<String, WAuctioneer> fAuctioneers;
	/** �����Ǘ� */
	private TreeMap<String, WOrderManager> fOrderManagers;
	/** ���i�n�� */
	private TreeMap<String, WPriceInfoDB> fPriceInfoDBs;

	/** �}�X�^�[�̏�� */
	private WServerStatusMaster fStatus;
	/** �}�X�^�[�̏�ԑJ�ڃ��b�N */
	private UReadWriteLock fStateLock;

	/** ���O�𐶐����Ȃ� */
	public static final int NO_LOG = 0;
	/** �ڍ׃��O�𐶐����� */
	public static final int DETAILED_LOG = 1;
	/** �ȈՃ��O�𐶐����� */
	public static final int SIMPLE_LOG = 2;
	/** ���O�̐������@(NO_LOG or DETAILED_LOG or SIMPLE_LOG)�̎w�� */
	private int fLogFlag;
	/** ���O�o�͐�f�B���N�g���� */
	private String fLogDir;
	/** �����˗��E������O�� */
	public final static String ORDER_LOG_DIR = "order";
	/** ����񃍃O�� */
	public final static String EXECUTION_LOG_DIR = "execution";
	/** ������񃍃O�� */
	public final static String ACCOUNT_LOG_DIR = "account";
	/** ������񃍃O�� */
	public final static String RESOURCE_LOG_DIR = "resource";

	/** �R�}���h���s�\�`�F�b�J�[ */
	private WCmdExecutableChecker fCmdExecutableChecker;

	/** ���K�[ */
	private static Logger log = Logger.getLogger(WMart.class);

	/**
	 * �R���X�g���N�^
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
	 * ���[�J���G�[�W�F���g���璍�����󂯂���
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
	 * ������i�߂�
	 * 
	 * @return �}�X�^�[���
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
				// �ŏ��̃Z�b�V������
				fStatus.setDate(1);
				fStatus.setSession(1);
				fStatus.setState(WServerStatusMaster.BUSINESS_HOURS);
				break;

			case WServerStatusMaster.BUSINESS_HOURS:
				// �Z�b�V�����̑O��
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
				// �Z�b�V�����̌㔼
				fStatus.setState(WServerStatusMaster.PREPARING_AUCTIONS);
				if (fAuctioneers.containsKey("spot")) {
					startAuction("spot", date, session);
				}
				if (fAuctioneers.containsKey("forward") && session == fSessionsPerDay) {
					startAuction("forward", date, session);
				}
				updateLog(fStatus.getDate(), fStatus.getSession(), fStatus.getState());
				// ���̃Z�b�V������
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
				// �ŏI�Z�b�V�����̌�
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
	 * ��菈�����J�n����
	 * 
	 * @param auctioneerName
	 *            �s�ꖼ
	 * @param date
	 *            ���݂̓�
	 * @param session
	 *            ���݂̐�
	 * @throws IOException
	 */
	private void startAuction(String auctioneerName, int date, int session) throws IOException {
		WAuctioneer au = fAuctioneers.get(auctioneerName);
		// ��V��������
		WBoard board = au.resetBoard();
		WOrderManager om = fOrderManagers.get(auctioneerName);
		// �I�[�_�[�� OrderManager ���� Board �ֈڂ�
		log.trace(om.toString());
		if (moveUncontractedOrdersToBoard(om, board)) {
			au.startAuction(date, session);
		}
	}

	/**
	 * �S�������̃I�[�_�[�� OrderManager ���� Board �ֈړ�����
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
	 * ��菈������������
	 * 
	 * @param auctioneerName
	 *            �s�ꖼ
	 * @param date
	 *            ���݂̓� (���̖�菈�����n�܂���B�����������菈���̓����1�X�e�b�v��ɂȂ�)
	 * @param session
	 *            ���݂̐� (���̖�菈�����n�܂�߁B�����������菈���̐߂��1�X�e�b�v��ɂȂ�)
	 * @throws IOException
	 */
	private void finishAuction(String auctioneerName, int date, int session) throws IOException {
		WAuctioneer au = fAuctioneers.get(auctioneerName);
		au.finishAuction();
		WBoard board = au.getBoard();
		WOrderManager om = fOrderManagers.get(auctioneerName);
		WPriceInfoDB pi = fPriceInfoDBs.get(auctioneerName);
		// ��艿�i�� AccountManager �ɓo�^����
		registerContractsToAccountManager(auctioneerName);
		// �s�ꉿ�i�� PriceInfoDB �ɓo�^����
		pi.addPriceInfo(au.getDate(), au.getSession(), au.getEarliestSlot(),
			board.getTotalPriceTable(), board.getTotalVolumeTable());
		pi.incrementPointer();
		// �I�[�_�[�� Board ���� OrderManager �ֈڂ�
		moveOrdersFromBoard(board, om);
		cancelFailedOrders(om, auctioneerName, date, session);
		// cancelOrdersOfBankruptedMembers(om);
		log.trace(om.toString());
		// ���M���O
		updateLogOfAuctioneer(auctioneerName);
		// �g���I�����������
		au.flushBoard();
	}

	/**
	 * �e�I�[�_�[�̖����� WAccountManager �ɓo�^����
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
	 * ���ׂẴI�[�_�[�� Board ���� OrderManager �ֈړ�����
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
	 * ��t���Ԃ��߂��Ă����ł��Ȃ������������L�����Z������
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
	 * �j�Y��������̒�����S�ăL�����Z������B
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
	 * ���݂̃}�X�^�[��Ԃ�Ԃ�
	 * 
	 * @return �}�X�^�[���
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
	 * �T�[�o�̐ݒ��W���o�͂���B
	 */
	public void printOn() {
		System.out.println("WMart is ready");
		System.out.println("No of boards per day: " + fSessionsPerDay);
	}

	/*
	 * �R�}���h���� ============================================================
	 */

	/**
	 * Login�R�}���h�ւ̉�������������B
	 * 
	 * @param userName
	 *            ���[�U�[��
	 * @param passwd
	 *            �p�X���[�h
	 * @return �Ή����郍�O�C�����(ULoginStatus)
	 */
	public ULoginStatus doLogin(String loginName, String passwd) {
		int userID = fAccountManager.getUserID(loginName, passwd);
		if (userID < 0) {
			return null;
		}
		return fLoginManager.findLoginStatus(userID);
	}

	/**
	 * Balances�R�}���h�ւ̉�������������B
	 * 
	 * @param todayHash
	 *            �������̎c���Ɖ���
	 * @param yesterdayHash
	 *            �O�����̎c���Ɖ���
	 * @param userID
	 *            ���[�UID
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
	 * Executions�R�}���h�ւ̉�������������B
	 * 
	 * @param arrayList
	 *            �n�b�V���}�b�v���i�[���邽�߂̃��X�g
	 * @param userID
	 *            ���[�UID
	 * @param auctioneerName
	 *            �s�ꖼ
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
	 * MarketPrice�R�}���h�ւ̉�������������B
	 * 
	 * @param arrayList
	 *            �n�b�V���}�b�v���i�[���邽�߂̃��X�g
	 * @param brandName
	 *            ������
	 * @param auctioneerName
	 *            �s�ꖼ
	 * @param noOfSteps
	 *            �ߋ����X�e�b�v���̉��i���K�v��?
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
	 * MarketStatus�R�}���h�ւ̉�������������B��������[�J�[�̏�Ԃ�Ԃ��B
	 * 
	 * @param hash
	 *            �e�����i�[���邽�߂̃n�b�V���}�b�v
	 * @param auctioneerName
	 *            �s�ꖼ
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
	 * MemberProfile�R�}���h�ւ̉�������������B
	 * 
	 * @param data
	 *            targetUserID�Ŏw�肳�ꂽ���[�U�[�̏����i�[���邽�߂�HashMap
	 * @param userID
	 *            ���̃R�}���h�����s�������[�U�[�̃��[�U�[ID
	 * @param targetUserID
	 *            ���ׂ������[�U�[�̃��[�U�[ID�i-1�̏ꍇ�CuserID����������j
	 * @return ���s���
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
			data.put(WCMemberProfileCore.STRING_LOGIN_NAME, account.getUserName()); // �����
			data.put(WCMemberProfileCore.STRING_PASSWORD, account.getPasswd()); // �p�X���[�h
			data.put(WCMemberProfileCore.STRING_ATTRIBUTE, account.getAttribute()); // �G�[�W�F���g����(Human
			// or Machine)
			data.put(WCMemberProfileCore.STRING_CONNECTION, account.getConnection()); // �R�l�N�V����(Remote
			// or Local)
			data.put(WCMemberProfileCore.ARRAY_LIST_ACCESS, account.getAccess()); // �A�N�Z�X����
			data.put(WCMemberProfileCore.STRING_REAL_NAME, account.getRealName()); // ���ۂ̖��O
			data.put(WCMemberProfileCore.ARRAY_LIST_SYSTEM_PARAMETERS,
				account.getSystemParameters()); // �V�X�e���p�����[�^
			data.put(WCMemberProfileCore.INT_SEED, new Integer(account.getSeed())); // �����̎�
			data.put(WCMemberProfileCore.LONG_INITIAL_CASH, new Long(account.getInitialCash())); // �������Y
			data.put(WCMemberProfileCore.LONG_TRADING_UNIT, new Long(account.getTradingUnit())); // ����P��
			data.put(WCMemberProfileCore.LONG_FEE_PER_UNIT, new Long(account.getFeePerUnit())); // �P�ʎ��������̎萔��
			data.put(WCMemberProfileCore.LONG_MARGIN_RATE, new Long(account.getMarginRate())); // �؋�����
			data.put(WCMemberProfileCore.LONG_MAX_LOAN, new Long(account.getMaxLoan())); // �؂������x�z
			data.put(WCMemberProfileCore.DOUBLE_INTEREST, new Double(account.getInterestPerYear())); // �؂�������
			data.put(WCMemberProfileCore.INT_STATUS, new Integer(account.getStatus())); // ����\(+1),
			// ����s�\(-1)
			data.put(WCMemberProfileCore.INT_NO_OF_LOGIN_AGENTS,
				new Integer(office.getNoOfLoginAgents())); // ���O�C�����̃G�[�W�F���g��
			HashMap yesterdayBalanceHash = new HashMap();
			WBalance yesterdayBalancel = account.getYesterdayBalance();
			setBalanceToHashMapForMemberProfile(yesterdayBalanceHash, yesterdayBalancel);
			data.put(WCMemberProfileCore.HASHMAP_YESTERDAY_BALANCE, yesterdayBalanceHash); // �O�����x
			HashMap todayBalanceHash = new HashMap();
			WBalance todayBalance = account.getTodayBalance();
			setBalanceToHashMapForMemberProfile(todayBalanceHash, todayBalance);
			data.put(WCMemberProfileCore.HASHMAP_TODAY_BALANCE, todayBalanceHash); // �������x
			HashMap positionHash = new HashMap();
			UPosition pos = account.getPosition();
			setPositionToHashMapForMemberProfile(positionHash, pos);
			data.put(WCMemberProfileCore.HASHMAP_POSITION, positionHash); // �|�W�V����
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
	 * OrderRequest�R�}���h�ւ̉�������������B
	 * 
	 * @param hash
	 *            �e�����i�[���邽�߂̃n�b�V���}�b�v
	 * @param userID
	 *            ���[�UID
	 * @param brandName
	 *            ���i�� (�s�g�p)
	 * @param auctioneerName
	 *            �s�ꖼ
	 * @param sellBuy
	 *            �����敪
	 * @param marketLimit
	 *            ���s�w�l�敪
	 * @param price
	 *            ��]������i
	 * @param spec
	 *            �������� "���i��:��]����:�ő�����:�Œx����:���׎���;" �̌J��Ԃ�
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
	 * OrderStatus�R�}���h�ւ̉�������������B
	 * 
	 * @param arrayList
	 *            �n�b�V���}�b�v���i�[���邽�߂̃��X�g
	 * @param userID
	 *            ���[�UID
	 * @param auctioneerName
	 *            �s�ꖼ
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
				// ���ςݒ����͒��߃Z�b�V�����Ő����������̂ݍڂ���
				for (int i = orders.getIndexOfLatestContractedOrder(); i < orders
					.getNoOfContractedOrders(); i++) {
					WOrder o = orders.getContractedOrderAt(i);
					HashMap hash = new HashMap();
					setOrderMessage(hash, o);
					arrayList.add(hash);
				}
				// ����蒍���͑S���ڂ���
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
	 * Position�R�}���h�ւ̉�������������B
	 * 
	 * @param hash
	 *            �e�����i�[���邽�߂̃n�b�V���}�b�v
	 * @param userID
	 *            ���[�UID
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
	 * ServerDate�R�}���h�ւ̉�������������B
	 * 
	 * @param hash
	 *            �e�����i�[���邽�߂̃n�b�V���}�b�v
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
	 * ServerStatus�R�}���h����������B������}�X�^�[�̏�Ԃ�Ԃ��B
	 * 
	 * @param data
	 *            �T�[�o�[��Ԃ��i�[���邽�߂�HashMap
	 * @param userID
	 *            ���̃R�}���h�����s�������[�U�[�̃��[�U�[ID
	 * @return ���s���
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
	 * ServerTime�R�}���h�ւ̉�������������B
	 * 
	 * @param hash
	 *            �e�����i�[���邽�߂̃n�b�V���}�b�v
	 */
	public WCommandStatus doServerTime(HashMap hash) {
		WCommandStatus ucs = new WCommandStatus();
		ucs.setStatus(true);
		hash.put(WCServerTimeCore.STRING_SERVER_TIME, extractTime(new Date()));
		return ucs;
	}

	/*
	 * �R�}���h�����̕⏕ ============================================================
	 */

	/**
	 * ���x���� hash �Ɋi�[����BdoBalances ����Ă΂��B
	 * 
	 * @param hash
	 *            �e�����i�[���邽�߂̃n�b�V���}�b�v
	 * @param balance
	 *            ���x���
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
	 * �I�[�_�[ o �̖���� info �� hash �Ɋi�[����BdoExecutions ����Ă΂��B
	 * 
	 * @param hash
	 *            �e�����i�[���邽�߂̃n�b�V���}�b�v
	 * @param o
	 *            �I�[�_�[
	 * @param info
	 *            �����
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
	 * ��ʃ��[�U�[�̎��x���� hash �Ɋi�[����DdoMemberProfile���� �Ă΂��D
	 * 
	 * @param hash
	 *            ��ʃ��[�U�[�̎��x�����i�[���邽�߂�HashMap
	 * @param bal
	 *            ��ʃ��[�U�[�̎��x���
	 */
	private void setBalanceToHashMapForMemberProfile(HashMap hash, WBalance bal) {
		long initialCash = bal.getInitialCash();
		hash.put(WCMemberProfileCore.LONG_INITIAL_CASH, new Long(initialCash)); // ����������
		long loan = bal.getLoan();
		hash.put(WCMemberProfileCore.LONG_LOAN, new Long(loan)); // �ؓ���
		long unrealizedProfit = bal.getUnrealizedProfit();
		hash.put(WCMemberProfileCore.LONG_UNREALIZED_PROFIT, new Long(unrealizedProfit)); // ���������v
		long margin = bal.getMargin();
		hash.put(WCMemberProfileCore.LONG_MARGIN, new Long(margin)); // �a���؋���
		long sumOfFee = bal.getSumOfFee();
		hash.put(WCMemberProfileCore.LONG_SUM_OF_FEE, new Long(sumOfFee)); // ���x�����萔��
		long sumOfInterest = bal.getSumOfInterest();
		hash.put(WCMemberProfileCore.LONG_SUM_OF_INTEREST, new Long(sumOfInterest)); // ���x��������
		long cash = bal.getCash();
		hash.put(WCMemberProfileCore.LONG_CASH, new Long(cash)); // �ۗL����
		long profit = bal.getProfit();
		hash.put(WCMemberProfileCore.LONG_PROFIT, new Long(profit)); // �������v
	}

	/**
	 * ��ʃ��[�U�[�̃|�W�V�������� hash �Ɋi�[����DdoMemberProfile ����Ă΂��D
	 * 
	 * @param hash
	 *            ��ʃ��[�U�[�̃|�W�V���������i�[���邽�߂�HashMap
	 * @param pos
	 *            ��ʃ��[�U�[�̃|�W�V�������
	 */
	private void setPositionToHashMapForMemberProfile(HashMap hash, UPosition pos) {
		long sumOfSellPositionsUntilYesterday = pos.getSumOfSellPositionUntilYesterday();
		hash.put(WCMemberProfileCore.LONG_SUM_OF_SELL_POSITIONS_UNTIL_YESTERDAY, new Long(
			sumOfSellPositionsUntilYesterday)); // �O���܂ł̔��|�W�V�����̍��v
		long sumOfBuyPositionsUntilYesterday = pos.getSumOfBuyPositionUntilYesterday();
		hash.put(WCMemberProfileCore.LONG_SUM_OF_BUY_POSITIONS_UNTIL_YESTERDAY, new Long(
			sumOfBuyPositionsUntilYesterday)); // �O���܂ł̔��|�W�V�����̍��v
		long todaySellPositions = pos.getSumOfTodaySellPosition();
		hash.put(WCMemberProfileCore.LONG_TODAY_SELL_POSITIONS, new Long(todaySellPositions)); // �����̔��|�W�V�����̍��v
		long todayBuyPositions = pos.getSumOfTodayBuyPosition();
		hash.put(WCMemberProfileCore.LONG_TODAY_BUY_POSITIONS, new Long(todayBuyPositions)); // �����̔��|�W�V�����̍��v
	}

	/**
	 * �I�[�_�[ o �̊e�f�[�^�� hash �Ɋi�[����BdoOrderStatus ����Ă΂��B
	 * 
	 * @param hash
	 *            �����i�[���邽�߂̃n�b�V���}�b�v
	 * @param o
	 *            ����
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
	 * date��"hh:mm:ss"�`���ɕϊ�����B
	 * 
	 * @param date
	 *            ������
	 */
	private String extractTime(Date date) {
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}

	/*
	 * ���M���O ============================================================
	 */

	/**
	 * ���O���X�V����D
	 * 
	 * @param date
	 *            ���݂̓��t
	 * @param session
	 *            ���݂̐�
	 * @param state
	 *            �}�[�P�b�g���
	 */
	private void updateLog(int date, int session, int state) throws IOException {
		if (fLogFlag == WMart.NO_LOG) {
			return;
		}
		switch (state) {
		case WServerStatusMaster.CONCLUDING_AUCTIONS: // �O���I����
		case WServerStatusMaster.PREPARING_AUCTIONS: // �㔼�I����
			if (fLogFlag == WMart.DETAILED_LOG) {
				writeOrderCommandLog(date, session);
				writeAccountLog(date, session);
			}
			fOrderCommandLog.clear();
			break;
		case WServerStatusMaster.AFTER_TRADING: // �S����I����
			if (fLogFlag == WMart.DETAILED_LOG) {
				writeAccountLog(date, session);
			}
			break;
		default:
			System.err.println("Invalid state " + state + " in WMart.updateLog()");
		}
	}

	/**
	 * ����/����R�}���h�̗������t�@�C���ɏ����o��
	 */
	private void writeOrderCommandLog(int date, int session) throws IOException {
		String filepath = String.format(path("%s/%s/order%03d%02d.csv"), fLogDir,
			WMart.ORDER_LOG_DIR, date, session);
		PrintWriter pw = new PrintWriter(new FileOutputStream(filepath));
		fOrderCommandLog.writeTo(pw);
		pw.close();
	}

	/**
	 * �����̗������t�@�C���ɏ����o��
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
	 * �e�s�ꂲ�Ƃ̃��O���X�V����
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
	 * ���i�n����t�@�C���ɏ����o��
	 */
	private void writePriceInfo(int date, int session, String auctioneerName) throws IOException {
		String filepath = String.format(path("%s/price_%s.csv"), fLogDir, auctioneerName);
		PrintWriter pw = new PrintWriter(new FileOutputStream(filepath, true));
		WPriceInfoDB pi = fPriceInfoDBs.get(auctioneerName);
		pi.writeLatestPriceInfo(pw);
		pw.close();
	}

	/**
	 * ��藚�����t�@�C���ɏ����o��
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
	 * MemberLog���t�@�C���ɏ����o���D(���������ɌĂ΂��)
	 * 
	 * @param filepath
	 *            String �o�̓t�@�C����
	 * @throws IOException
	 */
	private void writeMemberLog(String filepath) throws IOException {
		PrintWriter pw = new PrintWriter(new FileOutputStream(filepath));
		fMemberLog.writeTo(pw);
		pw.close();
	}

	/**
	 * �p�X��؂蕶�������ɍ��킹��
	 */
	private String path(String slashSeparatedPath) {
		return slashSeparatedPath.replace('/', File.separatorChar);
	}

	/*
	 * ������ ============================================================
	 */

	/**
	 * ���O�̏��������s���D
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void initLog() throws FileNotFoundException, IOException {
		initLog(null, false);
	}

	/**
	 * ���O�̏��������s���D
	 * 
	 * @param params
	 *            "logdir=(path)" ���܂ރV�X�e���p�����[�^ (�R������؂蕶����)�B<br />
	 *            "logdir=(path)" ���܂܂Ȃ��ꍇ�A�o�͐�f�B���N�g������ YYMMDD-hhmmss �ƂȂ�
	 * @param isSimple
	 *            true:�ȈՃ��O false:�ڍ׃��O
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
	 * ���O�o�͐�f�B���N�g�������擾
	 * 
	 * @return ���O�o�͐�f�B���N�g����
	 */
	public String getLogDir() {
		return fLogDir;
	}

	/**
	 * �V�X�e���p�����[�^���擾����
	 * 
	 * @param paramStr
	 *            �V�X�e���p�����[�^
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
	 * YYMMDD-hhmmss ��Ԃ�
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
	 * �V�����f�B���N�g�����쐬����B�����̃f�B���N�g�������������ɒ��g���ƍ폜����B
	 * 
	 * @param name
	 *            �f�B���N�g����
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
	 * �t�@�C���^�f�B���N�g�����ċA�I�ɍ폜����
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
	 * fAuctioneers �̏����ݒ���s��
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
	 * fPriceInfoDBs �̏����ݒ���s��
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
	 * fOrderManagers �̏����ݒ���s���B ���O�� fAccountManager �����������邱�ƁB
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
	 * �l�b�g���[�N���p�܂��̓X�^���h�A�������p�̃��O�C���}�l�[�W���𐶐�����D
	 * 
	 * @return ���O�C���}�l�[�W��
	 */
	private ULoginManager createLoginManager() {
		return new ULoginManager();
	}

	/**
	 * fLoginManger �̏����ݒ���s���B ���O�� fAccountManager �����������邱�ƁB
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
	 * fCmdExecutableChecker �̏����ݒ���s���B
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
	 * ���[�J���z�X�g�œ��삷��G�[�W�F���g��o�^����D
	 * 
	 * @param agent
	 *            WBaseStrategy���p�������G�[�W�F���g
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