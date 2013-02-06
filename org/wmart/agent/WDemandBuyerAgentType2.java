package org.wmart.agent;

/**
 * 買いエージェント。現物市場に組合せ注文する (Type2)
 * 
 * @author Ikki Fujiwara, NII
 */
public class WDemandBuyerAgentType2 extends WDemandBuyerAgent {

	/**
	 * コンストラクタ
	 */
	public WDemandBuyerAgentType2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
		fAuctioneer = "spot";
		fSplitSlot = true;
		fSplitGood = false;
	}
}
