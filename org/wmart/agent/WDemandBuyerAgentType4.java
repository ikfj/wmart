package org.wmart.agent;

/**
 * 買いエージェント。先物市場に組合せ注文する (Type4)
 * 
 * @author Ikki Fujiwara, NII
 */
public class WDemandBuyerAgentType4 extends WDemandBuyerAgent {

	/**
	 * コンストラクタ
	 */
	public WDemandBuyerAgentType4(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
		fAuctioneer = "forward";
		fSplitSlot = false;
		fSplitGood = false;
	}
}
