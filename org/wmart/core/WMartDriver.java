package org.wmart.core;

import java.util.*;

import org.umart.serverCore.*;

/**
 * マスタードライバ
 * 
 * @author Ikki Fujiwara, NII
 * 
 */
public class WMartDriver {
	
	/** 取引所マスター */
	private WMart fWMart = null;
	
	public static void main(String[] args) {
		WMartDriver driver = new WMartDriver();
		driver.doLoop();
	}
	
	/**
	 * コンストラクタ
	 */
	public WMartDriver() {
		UMemberList memberInfo = new UMemberList();
		memberInfo.appendMember("member1", "passwd1", "Machine", "Local", new ArrayList(),
			"member1", new ArrayList(), 1, 1000000000, 1000, 10000, 300000, 30000000, 0.1);
		memberInfo.appendMember("member2", "passwd2", "Machine", "Local", new ArrayList(),
			"member2", new ArrayList(), 1, 1000000000, 1000, 10000, 300000, 30000000, 0.1);
		memberInfo.appendMember("member3", "passwd3", "Machine", "Local", new ArrayList(),
			"member3", new ArrayList(), 1, 1000000000, 1000, 10000, 300000, 30000000, 0.1);
		memberInfo.appendMember("member4", "passwd4", "Machine", "Local", new ArrayList(),
			"member4", new ArrayList(), 1, 1000000000, 1000, 10000, 300000, 30000000, 0.1);
		memberInfo.appendMember("member5", "passwd5", "Machine", "Local", new ArrayList(),
			"member5", new ArrayList(), 1, 1000000000, 1000, 10000, 300000, 30000000, 0.1);
		long randomSeed = 0;
		int maxDate = 2;
		int sessionsPerDay = 2;
		int sessionsForward = sessionsPerDay * 4;
		int maxLength = sessionsForward / 2;
		fWMart = new WMart(memberInfo, randomSeed, maxDate, sessionsPerDay, sessionsForward,
			maxLength);
		try {
			fWMart.initLog("log", false); // true:簡易ログ、false:詳細ログ
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * メインループ
	 */
	public void doLoop() {
		System.out.println("main loop started");
		while (true) {
			WServerStatusMaster status = fWMart.nextStatus();
			if (status.getState() == WServerStatusMaster.BUSINESS_HOURS) {
				System.out.println("        === accepting human orders ===");
			}
			if (status.getState() == WServerStatusMaster.AFTER_HOURS) {
				System.out.println("        === accepting machine orders ===");
				orderRequest();
			}
			if (status.getState() == WServerStatusMaster.SHUTDOWN) {
				break;
			}
		}
		System.out.println("main loop finished");
	}
	
	private void orderRequest() {
		// 商品名:希望数量:最早時刻:最遅時刻:延べ時間;
		fWMart.doOrderRequest(new HashMap(), 1, "", "forward", WOrder.SELL, WOrder.LIMIT, 40,
			"serviceA:40:5:8:4;");
		fWMart.doOrderRequest(new HashMap(), 2, "", "forward", WOrder.SELL, WOrder.LIMIT, 15,
			"serviceB:30:5:8:4;");
		fWMart.doOrderRequest(new HashMap(), 3, "", "forward", WOrder.SELL, WOrder.LIMIT, 9,
			"serviceB:30:7:8:2;");
		fWMart.doOrderRequest(new HashMap(), 4, "", "forward", WOrder.BUY, WOrder.LIMIT, 60,
			"serviceA:20:6:8:3;serviceB:20:6:8:3;");
		fWMart.doOrderRequest(new HashMap(), 5, "", "forward", WOrder.BUY, WOrder.LIMIT, 40,
			"serviceA:10:5:7:3;serviceB:30:8:8:1;");
	}
}
