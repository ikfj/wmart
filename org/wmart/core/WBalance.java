package org.wmart.core;

import java.io.*;

/**
 * ûxî•ñ‚ğˆµ‚¤ƒNƒ‰ƒX‚Å‚·D ûxî•ñ‚Æ‚µ‚Ä‚ÍC‰ŠúŠ‹àCØ“ü‹àC–¢ÀŒ»‘¹‰vC—a‘õØ‹’‹àC ‘x•¥‚¢è”—¿C‘x•¥‚¢‹à—˜C•Û—LŒ»‹àCÀŒ»‘¹‰v‚ğˆµ‚Á‚Ä‚¢‚Ü‚·D
 * 
 * @author ì•” —Ti
 * @author X‰º —Ì•½
 * @author ¬–ì Œ÷
 * @author Ikki Fujiwara, NII
 */
public class WBalance {

	/** ‰ŠúŠ‹à */
	private long fInitialCash;

	/** Ø“ü‹à */
	private long fLoan;

	/** –¢ÀŒ»‘¹‰v */
	private long fUnrealizedProfit;

	/** —a‘õØ‹’‹à */
	private long fMargin;

	/** ‘x•¥‚¢è”—¿ */
	private long fSumOfFee;

	/** ‘x•¥‚¢‹à—˜ */
	private long fSumOfInterest;

	/** •Û—LŒ»‹à */
	private long fCash;

	/** ÀŒ»‘¹‰v */
	private long fProfit;

	/**
	 * ûxî•ñ‚ğì¬‚·‚éD
	 */
	public WBalance() {
		fInitialCash = 0;
		fLoan = 0;
		fUnrealizedProfit = 0;
		fMargin = 0;
		fSumOfFee = 0;
		fSumOfInterest = 0;
		fCash = 0;
		fProfit = 0;
	}

	/**
	 * •¡»‚ğ•Ô‚·D
	 * 
	 * @return •¡»
	 */
	public Object clone() {
		WBalance result = new WBalance();
		result.fInitialCash = fInitialCash;
		result.fLoan = fLoan;
		result.fUnrealizedProfit = fUnrealizedProfit;
		result.fMargin = fMargin;
		result.fSumOfFee = fSumOfFee;
		result.fSumOfInterest = fSumOfInterest;
		result.fCash = fCash;
		result.fProfit = fProfit;
		return result;
	}

	/**
	 * “à•”î•ñ‚ğo—Í‚·‚éD
	 * 
	 * @param pw
	 *            o—Íæ
	 */
	public void printOn(PrintWriter pw) {
		try {
			pw.println("fInitialCash = " + fInitialCash);
			pw.println("fCash = " + fCash);
			pw.println("fProfit = " + fProfit);
			pw.println("fUnrealizedProfit = " + fUnrealizedProfit);
			pw.println("fMargin = " + fMargin);
			pw.println("fSumOfFee = " + fSumOfFee);
			pw.println("fLoan = " + fLoan);
			pw.println("fSumOfInterest = " + fSumOfInterest);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	/**
	 * ‰ŠúŠ‹à‚ğ•Ô‚·D
	 * 
	 * @return ‰ŠúŠ‹à
	 */
	public long getInitialCash() {
		return fInitialCash;
	}

	/**
	 * Ø“ü‹à‚ğ•Ô‚·D
	 * 
	 * @return Ø“ü‹à
	 */
	public long getLoan() {
		return fLoan;
	}

	/**
	 * –¢ÀŒ»‘¹‰v‚ğ•Ô‚·D
	 * 
	 * @return –¢ÀŒ»‘¹‰v
	 */
	public long getUnrealizedProfit() {
		return fUnrealizedProfit;
	}

	/**
	 * —a‘õØ‹’‹à‚ğ•Ô‚·D
	 * 
	 * @return —a‘õØ‹’‹à
	 */
	public long getMargin() {
		return fMargin;
	}

	/**
	 * ‘x•¥‚¢è”—¿‚ğ•Ô‚·D
	 * 
	 * @return ‘x•¥‚¢è”—¿
	 */
	public long getSumOfFee() {
		return fSumOfFee;
	}

	/**
	 * ‘x•¥‹à—˜‚ğ•Ô‚·D
	 * 
	 * @return ‘x•¥‚¢‹à—˜
	 */
	public long getSumOfInterest() {
		return fSumOfInterest;
	}

	/**
	 * •Û—LŒ»‹à‚ğ•Ô‚·D
	 * 
	 * @return •Û—LŒ»‹à
	 */
	public long getCash() {
		return fCash;
	}

	/**
	 * ÀŒ»‘¹‰v‚ğ•Ô‚·D
	 * 
	 * @return ÀŒ»‘¹‰v
	 */
	public long getProfit() {
		return fProfit;
	}

	/**
	 * ‰ŠúŠ‹à‚ğİ’è‚·‚éD
	 * 
	 * @param initialCash
	 *            ‰ŠúŠ‹à
	 */
	public void setInitialCash(long initialCash) {
		fInitialCash = initialCash;
	}

	/**
	 * Ø“ü‹à‚ğİ’è‚·‚éD
	 * 
	 * @param loan
	 *            Ø“ü‹à
	 */
	public void setLoan(long loan) {
		fLoan = loan;
	}

	/**
	 * –¢ÀŒ»‘¹‰v‚ğİ’è‚·‚éD
	 * 
	 * @param unrealizedProfit
	 *            –¢ÀŒ»‘¹‰v
	 */
	public void setUnrealizedProfit(long unrealizedProfit) {
		fUnrealizedProfit = unrealizedProfit;
	}

	/**
	 * —a‘õØ‹’‹à‚ğİ’è‚·‚éD
	 * 
	 * @param margin
	 *            —a‘õØ‹’‹à
	 */
	public void setMargin(long margin) {
		fMargin = margin;
	}

	/**
	 * ‘x•¥‚¢è”—¿‚ğİ’è‚·‚éD
	 * 
	 * @param sumOfFee
	 *            ‘x•¥‚¢è”—¿
	 */
	public void setSumOfFee(long sumOfFee) {
		fSumOfFee = sumOfFee;
	}

	/**
	 * ‘x•¥‚¢‹à—˜‚ğİ’è‚·‚éD
	 * 
	 * @param interest
	 *            ‘x•¥‚¢‹à—˜
	 */
	public void setSumOfInterest(long interest) {
		fSumOfInterest = interest;
	}

	/**
	 * ÀŒ»‘¹‰v‚ğİ’è‚·‚éD
	 * 
	 * @param profit
	 *            ÀŒ»‘¹‰v
	 */
	public void setProfit(long profit) {
		fProfit = profit;
	}

	/**
	 * •Û—LŒ»‹à‚ğXV‚·‚éD
	 */
	public void updateCash() {
		fCash = fInitialCash + fUnrealizedProfit + fProfit + fLoan - fMargin - fSumOfFee - fSumOfInterest;
	}

}
