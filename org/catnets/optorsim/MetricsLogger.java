package org.catnets.optorsim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author streitberger
 */
public class MetricsLogger {

	private static String SEPARATOR = " ";

	private static MetricsLogger logger = null;

	private BufferedWriter output = null;

	private HashMap<String, BufferedWriter> outputList = new HashMap<String, BufferedWriter>();

	private MetricsLogger() {
		// make a new directory
		String dirName = "./log/" + System.currentTimeMillis();
		File f = new File(dirName);
		f.mkdir();

		String path = dirName + "/";
		String[] metrics = { "simulation_time", "resource_usage",
				"service_usage", "accepts", "rejects", "cfps",
				"complex_service_agent_allocation_rate",
				"successful_CS_requests", "total_CS_requests",
				"basic_service_provisioning_time",
				"complex_service_provisioning_time",
				"market_price_resource_central", "resource_seller_bid_central",
				"resource_buyer_bid_central", "service_seller_bid_central",
				"service_buyer_bid_central", "market_price_service_central",
				"util_satisfaction_buyer_resource_central",
				"util_satisfaction_seller_resource_central",
				"util_satisfaction_buyer_service_central",
				"util_satisfaction_seller_service_central",
				"negotiation_messages", "basic_service_allocation_time",
				"resource_allocation_time", "distance", "latency",
				"CS_BS_Mapping", "BS_R_Mapping", "cfps_sent",
				"util_satisfaction_resource_seller_decentral",
				"util_satisfaction_resource_buyer_decentral",
				"util_satisfaction_service_seller_decentral",
				"util_satisfaction_service_buyer_decentral",
				"catallactic_strategy_RA", "catallactic_strategy_CSA",
				"catallactic_strategy_BSA_buyer",
				"catallactic_strategy_BSA_seller", "csa_demand_distribution",
				"catallactic_initial_price_range", "trade_service_central", 
				"trade_resource_central", "proposals_decentral" };

		for (int i = 0; i < metrics.length; i++) {
			String file = metrics[i] + ".txt";
			try {
				output = new BufferedWriter(new FileWriter(path + file), 20000);
//				System.out.println(path + file);
				outputList.put(metrics[i], output);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}

		}// end for
	}// end constructor

	public static MetricsLogger instance() {
		if (logger == null)
			logger = new MetricsLogger();
		return logger;
	}

	public synchronized void logMetric(long timeStamp, String agentId,
			String name, String value) {

		String toLog = timeStamp + SEPARATOR;
		if (agentId != null) {
			toLog += agentId.toString() + SEPARATOR;
		} else {
			toLog += SEPARATOR;
		}
		toLog = toLog + name + SEPARATOR;
		toLog += value;
		// System.out.println(""+toLog);
		try {
			output = outputList.get(name);
			output.write(toLog);
			output.newLine();
			output.flush();
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			output.close();
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

}