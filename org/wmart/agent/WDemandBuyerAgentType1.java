package org.wmart.agent;

/**
 * �����G�[�W�F���g�B�����s��ɒP�i�������� (Type1)
 * 
 * @author Ikki Fujiwara, NII
 */
public class WDemandBuyerAgentType1 extends WDemandBuyerAgent {

	/**
	 * �R���X�g���N�^
	 */
	public WDemandBuyerAgentType1(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
		fAuctioneer = "spot";
		fSplitSlot = true;
		fSplitGood = true;
	}
}
