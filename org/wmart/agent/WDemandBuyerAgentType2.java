package org.wmart.agent;

/**
 * �����G�[�W�F���g�B�����s��ɑg������������ (Type2)
 * 
 * @author Ikki Fujiwara, NII
 */
public class WDemandBuyerAgentType2 extends WDemandBuyerAgent {

	/**
	 * �R���X�g���N�^
	 */
	public WDemandBuyerAgentType2(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
		fAuctioneer = "spot";
		fSplitSlot = true;
		fSplitGood = false;
	}
}
