package org.wmart.agent;

/**
 * 買いエージェント。先物市場に単品注文する (Type3)
 * 
 * @author Ikki Fujiwara, NII
 */
public class WDemandBuyerAgentType3 extends WDemandBuyerAgent {

	/**
	 * コンストラクタ
	 */
	public WDemandBuyerAgentType3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
		fAuctioneer = "forward";
		fSplitSlot = false;
		fSplitGood = true;
	}
}
