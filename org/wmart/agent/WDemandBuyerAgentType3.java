package org.wmart.agent;

/**
 * �����G�[�W�F���g�B�敨�s��ɒP�i�������� (Type3)
 * 
 * @author Ikki Fujiwara, NII
 */
public class WDemandBuyerAgentType3 extends WDemandBuyerAgent {

	/**
	 * �R���X�g���N�^
	 */
	public WDemandBuyerAgentType3(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
		fAuctioneer = "forward";
		fSplitSlot = false;
		fSplitGood = true;
	}
}
