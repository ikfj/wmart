package org.wmart.agent;

/**
 * �����G�[�W�F���g�B�敨�s��ɑg������������ (Type4)
 * 
 * @author Ikki Fujiwara, NII
 */
public class WDemandBuyerAgentType4 extends WDemandBuyerAgent {

	/**
	 * �R���X�g���N�^
	 */
	public WDemandBuyerAgentType4(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
		fAuctioneer = "forward";
		fSplitSlot = false;
		fSplitGood = false;
	}
}
