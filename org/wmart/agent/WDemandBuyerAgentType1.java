package org.wmart.agent;

/**
 * 買いエージェント。現物市場に単品注文する (Type1)
 * 
 * @author Ikki Fujiwara, NII
 */
public class WDemandBuyerAgentType1 extends WDemandBuyerAgent {

	/**
	 * コンストラクタ
	 */
	public WDemandBuyerAgentType1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
		fAuctioneer = "spot";
		fSplitSlot = true;
		fSplitGood = true;
	}
}
