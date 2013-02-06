package org.umart.strategy;

import java.util.*;

import org.umart.cmdCore.*;
import org.umart.serverCore.*;
import org.umart.strategyCore.UBaseAgent;

/**
 * @author user
 * 
 *         To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates.
 *         To enable and disable the creation of type comments go to Window>Preferences>Java>Code Generation.
 */
public class Strategy extends UBaseAgent {

	public static final int NO_OF_SPOT_PRICES = 120;

	public static final int NO_OF_FUTURE_PRICES = 60;

	/**
	 * Constructor for Strategy.
	 */
	public Strategy() {
		super("NotInitialized", "NotInitialized", "NotInitialized", 1);
	}

	private int[] getSpotPrices() {
		UCSpotPriceCore cmd = (UCSpotPriceCore) fUmcp.getCommand("SpotPrice");
		cmd.setArguments("j30", Strategy.NO_OF_SPOT_PRICES);
		UCommandStatus status = cmd.doIt();
		if (status.getStatus() == false) {
			System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
			System.err.println(status.getErrorMessage() + " in Strategy.getSpotPrices");
			System.exit(5);
		}
		ArrayList spotList = cmd.getResults();
		if (spotList.size() != Strategy.NO_OF_SPOT_PRICES) {
			System.err.println("spotList.size() != this.NO_OF_SPOT_PRICES in Strategy.doActions");
			System.exit(5);
		}
		int[] spotPrices = new int[Strategy.NO_OF_SPOT_PRICES];
		for (int i = 0; i < Strategy.NO_OF_SPOT_PRICES; ++i) {
			HashMap elem = (HashMap) spotList.get(i);
			spotPrices[Strategy.NO_OF_SPOT_PRICES - i - 1] = (int) ((Long) elem.get(UCSpotPriceCore.LONG_PRICE))
				.longValue();
		}
		return spotPrices;
	}

	private int[] getFuturePrices() {
		UCFuturePriceCore cmd = (UCFuturePriceCore) fUmcp.getCommand("FuturePrice");
		cmd.setArguments("j30", Strategy.NO_OF_FUTURE_PRICES);
		UCommandStatus status = cmd.doIt();
		if (status.getStatus() == false) {
			System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
			System.err.println(status.getErrorMessage() + " in Strategy.getFuturePrices");
			System.exit(5);
		}
		ArrayList futureList = cmd.getResults();
		if (futureList.size() != Strategy.NO_OF_FUTURE_PRICES) {
			System.err.println("futureList.size():" + futureList.size() + " != this.NO_OF_FUTURE_PRICES"
				+ NO_OF_FUTURE_PRICES + " in Strategy.getFuturePrices");
			System.exit(5);
		}
		int[] futurePrices = new int[Strategy.NO_OF_FUTURE_PRICES];
		for (int i = 0; i < Strategy.NO_OF_FUTURE_PRICES; ++i) {
			HashMap elem = (HashMap) futureList.get(i);
			futurePrices[Strategy.NO_OF_FUTURE_PRICES - i - 1] = (int) ((Long) elem.get(UCFuturePriceCore.LONG_PRICE))
				.longValue();
		}
		return futurePrices;
	}

	private int getPosition() {
		UCPositionCore cmd = (UCPositionCore) fUmcp.getCommand("Position");
		UCommandStatus status = cmd.doIt();
		if (status.getStatus() == false) {
			System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID + " in Strategy.getPosition");
			System.err.println(status.getErrorMessage() + " in Strategy.getPosition");
			System.exit(5);
		}
		HashMap result = cmd.getResults();
		long todayBuy = ((Long) result.get(UCPositionCore.LONG_TODAY_BUY)).longValue();
		long todaySell = ((Long) result.get(UCPositionCore.LONG_TODAY_SELL)).longValue();
		long yesterdayBuy = ((Long) result.get(UCPositionCore.LONG_YESTERDAY_BUY)).longValue();
		long yesterdaySell = ((Long) result.get(UCPositionCore.LONG_YESTERDAY_SELL)).longValue();
		long buy = todayBuy + yesterdayBuy;
		long sell = todaySell + yesterdaySell;
		return (int) (buy - sell);
	}

	private long getMoney() {
		UCBalancesCore cmd = (UCBalancesCore) fUmcp.getCommand("Balances");
		UCommandStatus status = cmd.doIt();
		if (status.getStatus() == false) {
			System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
			System.err.println(status.getErrorMessage() + " in Strategy.getMoney");
			System.exit(5);
		}
		HashMap bal = cmd.getTodayResults();
		return ((Long) bal.get(UCBalancesCore.LONG_SURPLUS)).longValue();
	}

	private int getRestDay(int date, int boardNo, int maxDate, int noOfBoardsPerDay) {
		return (maxDate - date) * noOfBoardsPerDay + noOfBoardsPerDay - boardNo + 1;
	}

	public int getLatestPrice(int[] prices) {
		for (int i = prices.length - 1; i >= 0; --i) {
			if (prices[i] >= 0) {
				return prices[i];
			}
		}
		return -1;
	}

	/**
	 * @see org.umart.strategyCore.UBaseAgent#doActions()
	 */
	public void doActions(int date, int boardNo, int serverState, int maxDate, int noOfBoardsPerDay) {
		if (serverState != UServerStatus.ACCEPT_ORDERS) {
			return;
		}
		int[] spotPrices = getSpotPrices();
		int[] futurePrices = getFuturePrices();
		int pos = getPosition();
		long money = getMoney();
		int restDay = getRestDay(date, boardNo, maxDate, noOfBoardsPerDay);
		action(spotPrices, futurePrices, pos, money, restDay);
	}

	/**
	 * Place an order.<br>
	 * 
	 * @param spotPrices
	 *            : Time series of spot prices. It provides 120 elements from spotPrices[0] to spotPrices[119]. The
	 *            element spotPrices[119] is the latest.
	 * @param futurePrices
	 *            : Time series of futures price. It provides 60 elements from futurePrices[0] to futurePrices[59]. The
	 *            element futurePrices[59] is the latest. The elemens has spot price when it represents before start-up
	 *            date and they contains -1 when nothing is executed.
	 * @param pos
	 *            : Position of the agent. Positive value represents long position and negative represents short
	 *            position.
	 * @param money
	 *            : Available cash. Note that type 'long' is used because of needed precision.
	 * @param restDay
	 *            : Number of remaining transaction to the closing of the market.
	 * @return Order: Order.price, Order.quant, Order.busell (= Order.SELL/Order.BUY/Order.NONE) should be set.
	 */
	protected Order getOrder(int[] spotPrices, int[] futurePrices, int pos, long money, int restDay) {
		return null;
	}

	public void action(int[] spotPrices, int[] futurePrices, int pos, long money, int restDay) {
		Order o = getOrder(spotPrices, futurePrices, pos, money, restDay);
		if (o == null) {
			System.err.println("Strategy.getOrder returned null in Strrategy.action");
			return;
		}
		orderRequest(o);
	}

	protected void orderRequest(Order o) {
		UCOrderRequestCore cmd = (UCOrderRequestCore) fUmcp.getCommand("OrderRequest");
		int sellBuy;
		if (o.buysell == Order.BUY) {
			sellBuy = UOrder.BUY;
		} else if (o.buysell == Order.SELL) {
			sellBuy = UOrder.SELL;
		} else {
			return;
		}
		if (o.price <= 0 || o.quant <= 0) {
			// System.err.println("ERORR!!!");
			// System.err.println("price=" + o.price + ", quant=" + o.quant);
			try {
				throw new Exception();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				return;
			}
		}
		cmd.setArguments("j30", UOrder.NEW, sellBuy, UOrder.LIMIT, o.price, o.quant);
		UCommandStatus status = cmd.doIt();
		if (status.getStatus() == false) {
			System.err.println("UserName:" + fLoginName + ", UserID:" + fUserID);
			System.err.println(status.getErrorMessage() + " in Strategy.orderRequest");
		}
	}

}
