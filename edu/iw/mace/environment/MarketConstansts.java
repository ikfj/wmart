/*
 * Created on 03.09.2005
 *
 */
package edu.iw.mace.environment;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Some constants required for the (central) service market
 * 
 * @author Bjoern Schnizler, University of Karlsruhe (TH)
 * @author Joerg Botsch, University of Karlsruhe (TH)
 * @author Matthias Kunzelmann, Meet2Trade, University of Karlsruhe (TH)
 * @author Ikki Fujiwara, NII
 * @version 1.0
 */

public class MarketConstansts {

	// Name of the properties file
	private static final String propertiesName = "market_central.properties";

	private Properties properties = null;

	private static MarketConstansts _marketConstantsInstance = null;

	// Flag to denote a buyer
	public static final int BUY = 0;

	// Flag to denote a seller
	public static final int SELL = 1;

	// A set of constants to denote price, volume, and type of order
	public static String PRICE = "Price = Preis";

	public static String VOLUME = "Volume = Volume";

	public static final String ORDERTYPE = "ordertype";

	public static final String CDA = "Continous Double Auction";

	public static final String CallA = "Call Auction";

	/**
	 * Returns the single instance of the MarketConstants object, creating it if
	 * it has not already been instantiated.
	 * 
	 * @return The MarketConstants instance.
	 */
	public static MarketConstansts getInstance() {
		if (_marketConstantsInstance == null)
			_marketConstantsInstance = new MarketConstansts();

		return _marketConstantsInstance;
	}

	/**
	 * Private constructor
	 */
	private MarketConstansts() {

		properties = new Properties();

		try {
			FileInputStream fin = new FileInputStream(propertiesName);
			properties.load(fin);
			fin.close();
		} catch (Exception e) {
			System.out.println(e);
			System.out
					.println("Could not find the central market properties file");
			System.exit(1);
		}
	}

	/**
	 * @return <true> if the price on the service market serves as a maximum bid
	 *         for the resource market, <false> otherwise
	 */
	public boolean isUseServiceMarketPrice() {
		return Boolean.parseBoolean("basic.useServiceMarketPrice");
	}

	/**
	 * @return Returns the <b>k</b> value for the service market
	 */
	public double getServiceMarketKprice() {
		return Double.parseDouble(properties.getProperty("service.kprice"));

	}

	/**
	 * @return Returns the <b>k</b> value for the resource market
	 */
	public double getResourceMarketKprice() {
		return Double.parseDouble(properties.getProperty("resource.kprice"));
	}

	/**
	 * @return Returns the number of attributes a resource has on the resource
	 *         market
	 */
	public int getNumberOfResourceAttributes() {
		return Integer.parseInt(properties.getProperty("resource.numberattributes"));
	}

	/**
	 * @return <true> if valuation/reservation of unsuccessful agents should be
	 *         updated after each clearing period, <false> otherwise
	 */
	public boolean isUpdateUnsuccessfulResourceAgents() {
		return Boolean.parseBoolean(properties.getProperty("resource.updateunsuccessful"));
	}

	/**
	 * @return <true> imitate the decentral strategy, <false> use a normal
	 *         distribution
	 */
	public boolean isImitateStrategy() {
		return Boolean.parseBoolean(properties
				.getProperty("valuation.imitateStrategy"));
	}

	public double getValuationSmallestValue() {
		return Double.parseDouble(properties
				.getProperty("valuation.smallestvalue"));
	}

	/**
	 * @return Mean of the normal distribution
	 */
	public double getValuationNormalMean() {
		return Double.parseDouble(properties
				.getProperty("valuation.normal.mean"));
	}

	/**
	 * @return Standard deviation of the normal distribution
	 */
	public double getValuationNormalDeviation() {
		return Double.parseDouble(properties
				.getProperty("valuation.normal.deviation"));
	}

	/**
	 * @return Weight of the current market price
	 */
	public double getValuationMarketPriceWeight() {
		return Double.parseDouble(properties
				.getProperty("valuation.strategy.markepriceweight"));
	}

	/**
	 * @return Mean of the normal distribution to imitate mutation
	 */
	public double getValuationMutationNormalMean() {
		return Double.parseDouble(properties
				.getProperty("valuation.strategy.normalmutationmean"));
	}

	/**
	 * @return Standard deviation of the normal distribution to imitate mutation
	 */
	public double getValuationMutationNormalDeviation() {
		return Double.parseDouble(properties
				.getProperty("valuation.strategy.normalmutationdeviation"));
	}

	/**
	 * @return Mean of the normal distribution to imitate mutation
	 */
	public double getValuationPriceStepNormalMean() {
		return Double.parseDouble(properties
				.getProperty("valuation.strategy.normalpricestepmean"));
	}

	/**
	 * @return Standard deviation of the normal distribution to imitate mutation
	 */
	public double getValuationPriceStepNormalDeviation() {
		return Double.parseDouble(properties
				.getProperty("valuation.strategy.normalpricestepdeviation"));
	}

	/**
	 * @return Scaling factor for the generated valuations and reservation
	 *         prices
	 */
	public double getValuationBuyerSellerMultiplier() {
		return Double.parseDouble(properties
				.getProperty("valuation.strategy.buyersellermultiplier"));
	}

	/**
	 * @return Depth of the weighted average
	 */
	public int getValuationDepthWeightedAverage() {
		return Integer.parseInt(properties
				.getProperty("valuation.strategy.depthweightedaverage"));
	}

	public double getValuationColdStartValue() {
		return Double.parseDouble(properties
				.getProperty("valuation.strategy.coldstartvaluation"));
	}

}
