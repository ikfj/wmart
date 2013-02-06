package org.wmart.logger;

import java.io.*;
import java.util.*;

import org.umart.serverCore.UExchangeAccount;
import org.umart.serverCore.UPosition;
import org.wmart.core.*;

/**
 * �P�ߕ��̌������̃��O�������N���X�ł��D
 * 
 * @author ����@��
 */
public class WAccountLog {

	/** ���t���������߂̃L�[ */
	public static final String INT_DATE = "INT_DATE";

	/** �߂��������߂̃L�[ */
	public static final String INT_SESSION = "INT_SESSION";

	/** ���O�C�������������߂̃L�[ */
	public static final String STRING_LOGIN_NAME = "STRING_LOGIN_NAME";

	/** �p�X���[�h���������߂̃L�[ */
	public static final String STRING_PASSWORD = "STRING_PASSWORD";

	/** �����̔����ʐ����������߂̃L�[ */
	public static final String LONG_TODAY_SELL = "LONG_TODAY_SELL";

	/** �����̔����ʐ����������߂̃L�[ */
	public static final String LONG_TODAY_BUY = "LONG_TODAY_BUY";

	/** �O���܂ł̔����ʐ����������߂̃L�[ */
	public static final String LONG_YESTERDAY_SELL = "LONG_YESTERDAY_SELL";

	/** �O���܂ł̔����ʐ����������߂̃L�[ */
	public static final String LONG_YESTERDAY_BUY = "LONG_YESTERDAY_BUY";

	/** �����ʐ��̍��v���������߂̃L�[ */
	public static final String LONG_SELL = "LONG_SELL";

	/** �����ʐ��̍��v���������߂̃L�[ */
	public static final String LONG_BUY = "LONG_BUY";

	/** �������������������߂̃L�[ */
	public static final String LONG_INITIAL_CASH = "LONG_INITIAL_CASH";

	/** ���������v���������߂̃L�[ */
	public static final String LONG_UNREALIZED_PROFIT = "LONG_UNREALIZED_PROFIT";

	/** �������v���������߂̃L�[ */
	public static final String LONG_PROFIT = "LONG_PROFIT";

	/** �؋����z���������߂̃L�[ */
	public static final String LONG_MARGIN = "LONG_MARGIN";

	/** �����������߂̃L�[ */
	public static final String LONG_FEE = "LONG_FEE";

	/** ������Z���z���������߂̃L�[ */
	public static final String LONG_LOAN = "LONG_LOAN";

	/** �Z���������������߂̃L�[ */
	public static final String LONG_INTEREST = "LONG_INTEREST";

	/** �]�T���z���������߂̃L�[ */
	public static final String LONG_SURPLUS = "LONG_SURPLUS";

	/** �����c�����������߂̃L�[ */
	public static final String LONG_CASH = "LONG_CASH";

	/** ����\���(UAccount.AVAILABLE/UAccount.UNAVAILABLE)���������߂̃L�[ */
	public static final String INT_STATUS = "INT_STATUS";

	/** ���t */
	private int fDate;

	/** �� */
	private int fSession;

	/** ������� */
	private ArrayList fAccountArray;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param date
	 *            ���t
	 * @param session
	 *            ��
	 */
	public WAccountLog(int date, int session) {
		fDate = date;
		fSession = session;
		fAccountArray = new ArrayList();
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param date
	 *            ���t
	 * @param session
	 *            ��
	 * @param am
	 *            �����Ǘ���
	 */
	public WAccountLog(int date, int session, WAccountManager am) {
		this(date, session);
		UExchangeAccount ea = am.getExchangeAccount();
		String userName = ea.getUserName();
		String passwd = ea.getPasswd();
		long todaysSellPosition = 0;
		long todaysBuyPosition = 0;
		long sellPositionUntilYesterday = 0;
		long buyPositionUntilYesterday = 0;
		long sumOfSellPosition = ea.getSellPosition();
		long sumOfBuyPosition = ea.getBuyPosition();
		long initialCash = 0;
		long unrealizedProfit = 0;
		long profit = 0;
		long margin = 0;
		long sumOfFee = 0;
		long loan = 0;
		long sumOfInterest = 0;
		long surplus = 0;
		long cash = ea.getCash();
		int status = WAccount.AVAILABLE;
		HashMap info = makeAccountInfo(date, session, userName, passwd, todaysSellPosition, todaysBuyPosition,
			sellPositionUntilYesterday, buyPositionUntilYesterday, sumOfSellPosition, sumOfBuyPosition, initialCash,
			unrealizedProfit, profit, margin, sumOfFee, loan, sumOfInterest, surplus, cash, status);
		fAccountArray.add(info);
		Enumeration accounts = am.getAccounts();
		while (accounts.hasMoreElements()) {
			WAccount account = (WAccount) accounts.nextElement();
			userName = account.getUserName();
			passwd = account.getPasswd();
			UPosition pos = account.getPosition();
			todaysSellPosition = pos.getSumOfTodaySellPosition();
			todaysBuyPosition = pos.getSumOfTodayBuyPosition();
			sellPositionUntilYesterday = pos.getSumOfSellPositionUntilYesterday();
			buyPositionUntilYesterday = pos.getSumOfBuyPositionUntilYesterday();
			sumOfSellPosition = todaysSellPosition + sellPositionUntilYesterday;
			sumOfBuyPosition = todaysBuyPosition + buyPositionUntilYesterday;
			WBalance bal = account.getTodayBalance();
			initialCash = bal.getInitialCash();
			unrealizedProfit = bal.getUnrealizedProfit();
			profit = bal.getProfit();
			margin = bal.getMargin();
			sumOfFee = bal.getSumOfFee();
			loan = bal.getLoan();
			sumOfInterest = bal.getSumOfInterest();
			surplus = bal.getCash();
			cash = surplus + margin;
			status = account.getStatus();
			info = makeAccountInfo(date, session, userName, passwd, todaysSellPosition, todaysBuyPosition,
				sellPositionUntilYesterday, buyPositionUntilYesterday, sumOfSellPosition, sumOfBuyPosition,
				initialCash, unrealizedProfit, profit, margin, sumOfFee, loan, sumOfInterest, surplus, cash, status);
			fAccountArray.add(info);
		}
	}

	/**
	 * �������(HashMap)�𐶐�����D
	 * 
	 * @param date
	 *            ���t
	 * @param session
	 *            ��
	 * @param userName
	 *            ���[�U��
	 * @param passwd
	 *            �p�X���[�h
	 * @param todaysSellPosition
	 *            �������|�W�V����
	 * @param todaysBuyPosition
	 *            �������|�W�V����
	 * @param sellPositionUntilYesterday
	 *            �O���܂ł̔��|�W�V����
	 * @param buyPositionUntilYesterday
	 *            �O���܂ł̔��|�W�V����
	 * @param sumOfSellPosition
	 *            ���|�W�V�����̍��v
	 * @param sumOfBuyPosition
	 *            ���|�W�V�����̍��v
	 * @param initialCash
	 *            �����̏����l
	 * @param unrealizedProfit
	 *            ���������v
	 * @param profit
	 *            �������v
	 * @param margin
	 *            �؋���
	 * @param sumOfFee
	 *            �萔���̍��v
	 * @param loan
	 *            �ؓ���
	 * @param sumOfInterest
	 *            ���q�̍��v
	 * @param surplus
	 *            �]�T���z
	 * @param cash
	 *            �����c��
	 * @param status
	 *            �������
	 * @return �������
	 */
	private HashMap makeAccountInfo(int date, int session, String userName, String passwd, long todaysSellPosition,
		long todaysBuyPosition, long sellPositionUntilYesterday, long buyPositionUntilYesterday,
		long sumOfSellPosition, long sumOfBuyPosition, long initialCash, long unrealizedProfit, long profit,
		long margin, long sumOfFee, long loan, long sumOfInterest, long surplus, long cash, int status) {
		HashMap hash = new HashMap();
		hash.put(WAccountLog.INT_DATE, new Integer(date));
		hash.put(WAccountLog.INT_SESSION, new Integer(session));
		hash.put(WAccountLog.STRING_LOGIN_NAME, userName);
		hash.put(WAccountLog.STRING_PASSWORD, passwd);
		hash.put(WAccountLog.LONG_TODAY_SELL, new Long(todaysSellPosition));
		hash.put(WAccountLog.LONG_TODAY_BUY, new Long(todaysBuyPosition));
		hash.put(WAccountLog.LONG_YESTERDAY_SELL, new Long(sellPositionUntilYesterday));
		hash.put(WAccountLog.LONG_YESTERDAY_BUY, new Long(buyPositionUntilYesterday));
		hash.put(WAccountLog.LONG_SELL, new Long(sumOfSellPosition));
		hash.put(WAccountLog.LONG_BUY, new Long(sumOfBuyPosition));
		hash.put(WAccountLog.LONG_INITIAL_CASH, new Long(initialCash));
		hash.put(WAccountLog.LONG_UNREALIZED_PROFIT, new Long(unrealizedProfit));
		hash.put(WAccountLog.LONG_PROFIT, new Long(profit));
		hash.put(WAccountLog.LONG_MARGIN, new Long(margin));
		hash.put(WAccountLog.LONG_FEE, new Long(sumOfFee));
		hash.put(WAccountLog.LONG_LOAN, new Long(loan));
		hash.put(WAccountLog.LONG_INTEREST, new Long(sumOfInterest));
		hash.put(WAccountLog.LONG_SURPLUS, new Long(surplus));
		hash.put(WAccountLog.LONG_CASH, new Long(cash));
		hash.put(WAccountLog.INT_STATUS, new Integer(status));
		return hash;
	}

	/**
	 * �o�̓X�g���[���֏����o���D
	 * 
	 * @param pw
	 *            �o�̓X�g���[��
	 * @throws IOException
	 */
	public void writeTo(PrintWriter pw) throws IOException {
		pw.println("Date,Session,LoginName,Password,TodaysSellPosition,TodaysBuyPosition"
			+ ",SellPositionUntilYesterday,BuyPositionUntilYesterday" + ",SumOfSellPosition,SumOfBuyPosition"
			+ ",InitialCash,UnrealizedProfit,Profit,Margin,SumOfFee" + ",Loan,SumOfInterest,Surplus,Cash,Status");
		Iterator itr = fAccountArray.iterator();
		while (itr.hasNext()) {
			HashMap hash = (HashMap) itr.next();
			pw.print(hash.get(WAccountLog.INT_DATE) + ",");
			pw.print(hash.get(WAccountLog.INT_SESSION) + ",");
			pw.print(hash.get(WAccountLog.STRING_LOGIN_NAME) + ",");
			pw.print(hash.get(WAccountLog.STRING_PASSWORD) + ",");
			pw.print(hash.get(WAccountLog.LONG_TODAY_SELL) + ",");
			pw.print(hash.get(WAccountLog.LONG_TODAY_BUY) + ",");
			pw.print(hash.get(WAccountLog.LONG_YESTERDAY_SELL) + ",");
			pw.print(hash.get(WAccountLog.LONG_YESTERDAY_BUY) + ",");
			pw.print(hash.get(WAccountLog.LONG_SELL) + ",");
			pw.print(hash.get(WAccountLog.LONG_BUY) + ",");
			pw.print(hash.get(WAccountLog.LONG_INITIAL_CASH) + ",");
			pw.print(hash.get(WAccountLog.LONG_UNREALIZED_PROFIT) + ",");
			pw.print(hash.get(WAccountLog.LONG_PROFIT) + ",");
			pw.print(hash.get(WAccountLog.LONG_MARGIN) + ",");
			pw.print(hash.get(WAccountLog.LONG_FEE) + ",");
			pw.print(hash.get(WAccountLog.LONG_LOAN) + ",");
			pw.print(hash.get(WAccountLog.LONG_INTEREST) + ",");
			pw.print(hash.get(WAccountLog.LONG_SURPLUS) + ",");
			pw.print(hash.get(WAccountLog.LONG_CASH) + ",");
			pw.println(hash.get(WAccountLog.INT_STATUS) + "");
		}
	}

	/**
	 * ���̓X�g���[������ǂݍ��ށD
	 * 
	 * @param br
	 *            BufferedReader ���̓X�g���[��
	 * @throws IOException
	 */
	public void readFrom(BufferedReader br) throws IOException {
		fAccountArray.clear();
		br.readLine(); // skip the header
		String line = null;
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, ",");
			int date = Integer.parseInt(st.nextToken());
			int session = Integer.parseInt(st.nextToken());
			int userID = Integer.parseInt(st.nextToken());
			String userName = st.nextToken();
			String passwd = st.nextToken();
			long todaysSellPosition = Long.parseLong(st.nextToken());
			long todaysBuyPosition = Long.parseLong(st.nextToken());
			long sellPositionUntilYesterday = Long.parseLong(st.nextToken());
			long buyPositionUntilYesterday = Long.parseLong(st.nextToken());
			long sumOfSellPosition = Long.parseLong(st.nextToken());
			long sumOfBuyPosition = Long.parseLong(st.nextToken());
			long initialCash = Long.parseLong(st.nextToken());
			long unrealizedProfit = Long.parseLong(st.nextToken());
			long profit = Long.parseLong(st.nextToken());
			long margin = Long.parseLong(st.nextToken());
			long sumOfFee = Long.parseLong(st.nextToken());
			long loan = Long.parseLong(st.nextToken());
			long sumOfInterest = Long.parseLong(st.nextToken());
			long surplus = Long.parseLong(st.nextToken());
			long cash = Long.parseLong(st.nextToken());
			int status = Integer.parseInt(st.nextToken());
			HashMap info = makeAccountInfo(date, session, userName, passwd, todaysSellPosition, todaysBuyPosition,
				sellPositionUntilYesterday, buyPositionUntilYesterday, sumOfSellPosition, sumOfBuyPosition,
				initialCash, unrealizedProfit, profit, margin, sumOfFee, loan, sumOfInterest, surplus, cash, status);
			fAccountArray.add(info);
		}
	}

	/**
	 * �������iHashMap�j�̔z���Ԃ��D
	 * 
	 * @return ArrayList �������iHashMap�j�̔z��
	 */
	public ArrayList getAccountArray() {
		return fAccountArray;
	}

}