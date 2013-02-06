package org.catnets.optorsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import edu.iw.mace.environment.AbstractAgentPool;
import edu.iw.mace.environment.Agent;
import edu.iw.mace.environment.Attribute;
import edu.iw.mace.environment.Bundle;
import edu.iw.mace.environment.Good;
import edu.iw.mace.order.AbstractBundleOrder;
import edu.iw.mace.order.AttributesBundle;
import edu.iw.mace.order.BundleOrderBuyer;
import edu.iw.mace.order.BundleOrderSeller;
import edu.iw.mace.order.OrderBuyer;
import edu.iw.mace.order.OrderSeller;
import edu.iw.mace.outcome.AbstractOutcomeBean;
import edu.iw.mace.outcome.Outcome;
import edu.iw.mace.outcome.OutcomeBuyerBean;
import edu.iw.mace.outcome.OutcomeSellerBean;
import edu.iw.mace.outcome.OutcomeSellerSlotBean;
import edu.iw.mace.outcome.allocator.WinnerDeterminationFactory;

/**
 * ResourceMarketAuctioneerAgent class is a thread. It is started if a Resource Market Central
 * Auctioneer is present on a site. It can receive Negotiation objects coming from
 * BasicServiceAgents and negotiate with them.
 * <p>
 * Copyright (c) 2002 CERN, ITC-irst, PPARC, on behalf of the EU DataGrid. For license conditions
 * see LICENSE file or <a href="http://www.edg.org/license.html">http://www.edg.org/license.html</a>
 * <p>
 * 
 * @author Floriano Zini, ITC-irst
 * @author Bjoern Schnizler, University of Karlsruhe (TH)
 * 
 */
public class ResourceMarketAuctioneerAgent extends Thread implements Negotiator {

	private static Logger log = Logger.getLogger(ResourceMarketAuctioneerAgent.class);

	// the inbox of messages waiting to be read
	private List<Message> _messageQueue = new Vector<Message>();

	// made false when shutting down BSB
	private boolean _iAmAlive;

	// the local p2p mediator to communicate messages
	private P2P _p2pMediator;

	// Instance of GridTime
	private GridTime _time = GridTimeFactory.getGridTime();

	// Optorsim Parameter
	private OptorSimParameters _optorSimParameters = OptorSimParameters.getInstance();

	// Clearing policy
	private String _clearingPolicy = "";

	// Delay between two auction clearing calls
	private long _delayMS = 0;

	// Resource market instance
	private Market market = null;

	private static HashMap<String, Double> marketPrices = new HashMap<String, Double>();

	/**
	 * Construct a ResourceMarketAuctioneerAgent. Note caller must also start() this thread.
	 * 
	 * @param id
	 *            The numerical counter for this thread. Only used for display purposes
	 * @param rNames
	 *            Vector storing the names of each resource
	 * @param p2p
	 *            The P2PMediator used for communication
	 */
	public ResourceMarketAuctioneerAgent(int id, Vector rNames, P2P p2p) {
		super(GridTimeFactory.getThreadGroup(), "RMAA" + id + "@" + "CentralAuctioneerSite");
		_p2pMediator = p2p;
		_iAmAlive = true;

		// Setup the clearing policy parameters
		if (_optorSimParameters.getClearingPolicyResourceMarket() == 1) {
			_clearingPolicy = MarketConstansts.CallA;
			_delayMS = _optorSimParameters.getClearingIntervalResourceMarket();
		} else if (_optorSimParameters.getClearingPolicyResourceMarket() == 2)
			_clearingPolicy = MarketConstansts.CDA;

		try {
			market = new Market();
			WinnerDeterminationFactory
				.instance()
				.setValue(
					WinnerDeterminationFactory.MODELS[WinnerDeterminationFactory.CATNETS_RESOURCE_MARKET]);
		} catch (MaceException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		log.debug("Successful init of resource market");

		// If Call Market => waitDelay = 2min
		// else => waitDelay = 0

		// our caller starts us off
	}

	public void run() {

		Vector<Message> msgToProcess = new Vector<Message>();
		Vector<Message> deleteMsgToProcess = new Vector<Message>();

		while (_iAmAlive) {

			standBy(_delayMS);

			MarketAuctioneerAgentUtils.getMessages(_messageQueue, deleteMsgToProcess, msgToProcess);

			for (Iterator i = deleteMsgToProcess.iterator(); i.hasNext();) {
				Message msg = (Message) i.next();

				// Parse message content
				if (msg instanceof ResourceMarketDeleteOrderMessage) {
					// an order has to be removed
					// Find the correspondig agent in the agentPool
					CATNETSAgent agentMarket = getAgentFromPool(market.getEnvironment()
						.getBuyerPool(), msg.getMyNegotiation());
					boolean remove = false;
					if (agentMarket == null) {
						agentMarket = getAgentFromPool(market.getEnvironment().getSellerPool(), msg
							.getMyNegotiation());
						if (agentMarket != null) {
							remove = market.getOrderBookManagement().getOrderBook()
								.getSellerOrderPool().removeOrders(agentMarket);
							if (remove == true)
								// Bid was not succesfull -> write a "zero"
								// value into the util_satisfaction file
								MetricsLogger.instance()
									.logMetric(
										_time.getRunningTimeMillis(),
										msg.getMyNegotiation() + " "
											+ msg.getMyNegotiation().getNegotiator(),
										"util_satisfaction_seller_resource_central",
										"" + 0 + " remove");

						}
					} else {
						remove = market.getOrderBookManagement().getOrderBook().getBuyerOrderPool()
							.removeOrders(agentMarket);
						if (remove == true)
							// Bid was not succesfull -> write a "zero" value
							// into the util_satisfaction file
							MetricsLogger.instance().logMetric(
								_time.getRunningTimeMillis(),
								msg.getMyNegotiation() + " "
									+ msg.getMyNegotiation().getNegotiator(),
								"util_satisfaction_buyer_resource_central", "" + 0 + " remove");

					}
					if (MarketAuctioneerAgentUtils.removeDeleteMessageFromQueue(msg, msgToProcess) == true)
						log.debug("Removed DeleteMessage from Queue");
				}

			}
			for (Iterator i = msgToProcess.iterator(); i.hasNext();) {
				Message msg = (Message) i.next();
				// Transformation of message into an order
				String rName = msg.getMyNegotiation().getNegotiationObject().toString();

				if (msg instanceof ResourceMarketBuyerMessage) {
					// This is a buyer order
					double value = ((ResourceMarketBuyerMessage) msg).getPrice();
					OrderBuyer orderBuyer = null;
					try {
						orderBuyer = generateBuyerOrder((ResourceMarketBuyerMessage) msg);
						if (market.getOrderBookManagement().getOrderBook().getBuyerOrderPool()
							.getOrder(orderBuyer.getAgent()) == null) {
							market.getOrderBookManagement().getOrderBook().getBuyerOrderPool()
								.addOrder(orderBuyer);
						} else {
							log.error("Tried to overwrite a buyer order from "
								+ msg.getMyNegotiation());
						}
					} catch (MaceException e) {
						e.printStackTrace();
					}
					log
						.debug(" Generated a new buyer order for "
							+ orderBuyer.getAgent().getId()
							+ " in "
							+ msg.getMyNegotiation().toString()
							+ " with a value of "
							+ value
							+ ", bundleId="
							+ ((AbstractBundleOrder) orderBuyer.getBundleOrders().values()
								.toArray()[0]).getBundle().getId() + " " + rName);
				}

				else if (msg instanceof ResourceMarketSellerMessage) {
					// This is a seller order
					double value = ((ResourceMarketSellerMessage) msg).getPrice();
					OrderSeller orderSeller = null;
					try {
						orderSeller = generateSellerOrder((ResourceMarketSellerMessage) msg);
						if (market.getOrderBookManagement().getOrderBook().getSellerOrderPool()
							.getOrder(orderSeller.getAgent()) == null) {
							market.getOrderBookManagement().getOrderBook().getSellerOrderPool()
								.addOrder(orderSeller);
						} else {
							log.error("Tried to overwrite a seller order from "
								+ msg.getMyNegotiation());
						}

					} catch (MaceException e) {
						e.printStackTrace();
					}

					// order = new Order(msg.getMyNegotiation(), bsName,
					// MarketConstansts.SELL, (long) value);
					log
						.debug(" Generated a new seller order for "
							+ orderSeller.getAgent().getId()
							+ " in "
							+ msg.getMyNegotiation().toString()
							+ " with a value of "
							+ value
							+ ", bundleId="
							+ ((AbstractBundleOrder) orderSeller.getBundleOrders().values()
								.toArray()[0]).getBundle().getId() + " " + rName);
				}

				// Clear (in case of call market manualClearing)
				if (_clearingPolicy.equals(MarketConstansts.CDA)) {
					log.error("CDA on the resource market is currently not supported");
				}
			}
			deleteMsgToProcess.clear();
			msgToProcess.clear();

			if (_clearingPolicy.equals(MarketConstansts.CallA)) {
				if (market.getOrderBookManagement().getOrderBook().getBuyerOrderPool().size() > 0
					&& market.getOrderBookManagement().getOrderBook().getSellerOrderPool().size() > 0) {
					log.debug("I am starting to allocate on the resource market...");
					try {
						market.solve();
					} catch (MaceException e) {
						log.error(e.fillInStackTrace());
					}
					log.debug("Computed an outcome for the resource market with a welfare of "
						+ market.getOutcome().getWelfare());
					informAgents();
					updateMarketPrices();
					try {
						market.removeExecuted();
						if (MarketConstansts.getInstance().isUpdateUnsuccessfulResourceAgents())
							updateUnsuccessfulPrices();
					} catch (MaceException e) {
						log.error(e.fillInStackTrace());
					}
				}
			}
		}
	}

	/**
	 * This function updates the valuation and reservation prices of all unsuccessful agents.
	 */
	private void updateUnsuccessfulPrices() {

		/**
		 * Parse the buyer order book (all agents that are in the order book are NOT successful)
		 */
		for (int a = 0; a < market.getOrderBookManagement().getOrderBook().getBuyerOrderPool()
			.size(); a++) {
			OrderBuyer orderBuyer = market.getOrderBookManagement().getOrderBook()
				.getBuyerOrderPool().getBuyerOrder(a);
			BundleOrderBuyer bundleOrderBuyer = (BundleOrderBuyer) orderBuyer.getBundleOrders()
				.values().toArray()[0];
			BasicServiceAgent agent = (BasicServiceAgent) ((CATNETSAgent) orderBuyer.getAgent())
				.getNegotiation().getNegotiator();
			double newBid = agent.getRMValuationGenerator().computeValue(
				getLastPrice(bundleOrderBuyer.getBundle()), true);
			bundleOrderBuyer.setBid(newBid);
			bundleOrderBuyer.setValuation(newBid);
		}

		/**
		 * Parse the seller order book (all agents that are in the order book are NOT successful)
		 */
		for (int a = 0; a < market.getOrderBookManagement().getOrderBook().getSellerOrderPool()
			.size(); a++) {
			OrderSeller orderSeller = market.getOrderBookManagement().getOrderBook()
				.getSellerOrderPool().getSellerOrder(a);
			BundleOrderSeller bundleOrderSeller = (BundleOrderSeller) orderSeller.getBundleOrders()
				.values().toArray()[0];
			ResourceAgent agent = (ResourceAgent) ((CATNETSAgent) orderSeller.getAgent())
				.getNegotiation().getNegotiator();
			double newBid = agent.getValuationGenerator().computeValue(
				getLastPrice(bundleOrderSeller.getBundle()), true);
			bundleOrderSeller.setBid(newBid);
			bundleOrderSeller.setValuation(newBid);
		}
	}

	/**
	 * Generates a new buyer order for a given message
	 * 
	 * @param msg
	 *            ResourceMarketBuyerMessage
	 * @return New order
	 */
	private OrderBuyer generateBuyerOrder(ResourceMarketBuyerMessage msg) throws MaceException {
		// Get the agent
		String agentId = msg.getMyNegotiation().toString();
		// getNegotiator().toString();
		CATNETSAgent agent = (CATNETSAgent) market.getEnvironment().getBuyerPool()
			.getAgent(agentId);
		if (agent == null) {
			agent = new CATNETSAgent(agentId, Agent.AGENT_BUYER, msg.getMyNegotiation());
			market.getEnvironment().getBuyerPool().addAgent(agent);
		}
		// Generate a new instance of an order
		OrderBuyer orderBuyer = new OrderBuyer(agent);
		// Generate a new instance of an bundle order
		BundleOrderBuyer bundleOrderBuyer = new BundleOrderBuyer(orderBuyer);
		orderBuyer.addBundleOrder(bundleOrderBuyer);
		// Get the bundle
		Bundle bundle = getBundle((HashMap) msg.getMyNegotiation().getNegotiationObject());
		bundleOrderBuyer.setBundle(bundle);
		// Set the quality characteristics
		AttributesBundle attributesBundle = getAttributesBundle(bundle, (HashMap) msg
			.getMyNegotiation().getNegotiationObject());
		bundleOrderBuyer.setQuality(attributesBundle);
		// Set the bid and the valuation - in the CATNETS scenarios, bid and
		// valuation are equal
		bundleOrderBuyer.setBid(msg.getPrice());
		bundleOrderBuyer.setValuation(msg.getPrice());
		bundleOrderBuyer.setSlots(1);
		bundleOrderBuyer.setEarly(1);
		bundleOrderBuyer.setLatest(1);
		MetricsLogger.instance().logMetric(_time.getRunningTimeMillis(),
			"" + msg.getMyNegotiation() + " " + msg.getMyNegotiation().getNegotiator(),
			"resource_buyer_bid_central",
			"" + msg.getPrice() + " " + bundleOrderBuyer.getBundle().toString());
		// Return the order
		return orderBuyer;
	}

	/**
	 * Generates a new seller order for a given message
	 * 
	 * @param msg
	 *            ResourceMarketSellerMessage
	 * @return New order
	 */
	private OrderSeller generateSellerOrder(ResourceMarketSellerMessage msg) throws MaceException {
		// Get the agent
		String agentId = msg.getMyNegotiation().toString(); // getNegotiator().toString();
		CATNETSAgent agent = (CATNETSAgent) market.getEnvironment().getSellerPool().getAgent(
			agentId);
		if (agent == null) {
			agent = new CATNETSAgent(agentId, Agent.AGENT_SELLER, msg.getMyNegotiation());
			market.getEnvironment().getSellerPool().addAgent(agent);
		}
		// Generate a new instance of an order
		OrderSeller orderSeller = new OrderSeller(agent);
		// Generate a new instance of an bundle order
		BundleOrderSeller bundleOrderSeller = new BundleOrderSeller(orderSeller);
		orderSeller.addBundleOrder(bundleOrderSeller);
		// Get the bundle
		Bundle bundle = getBundle((HashMap) msg.getMyNegotiation().getNegotiationObject());
		bundleOrderSeller.setBundle(bundle);
		// Set the quality characteristics
		AttributesBundle attributesBundle = getAttributesBundle(bundle, (HashMap) msg
			.getMyNegotiation().getNegotiationObject());
		bundleOrderSeller.setQuality(attributesBundle);
		// Set the bid and the reservation - in the CATNETS scenarios, bid and
		// valuation are equal
		bundleOrderSeller.setBid(msg.getPrice());
		bundleOrderSeller.setValuation(msg.getPrice());
		bundleOrderSeller.setEarly(1);
		bundleOrderSeller.setLatest(1);
		MetricsLogger.instance().logMetric(_time.getRunningTimeMillis(),
			msg.getMyNegotiation() + " " + msg.getMyNegotiation().getNegotiator(),
			"resource_seller_bid_central",
			"" + msg.getPrice() + " " + bundleOrderSeller.getBundle().toString());
		// Return the order
		return orderSeller;
	}

	/**
	 * Returns a bundle for a given "HashMap-encoded transaction object"
	 * 
	 * @param negotiationObject
	 *            Transaction object
	 * @return Bundle
	 */
	private Bundle getBundle(HashMap negotiationObject) throws MaceException {
		Iterator iterator = negotiationObject.keySet().iterator();
		Bundle bundle = new Bundle();
		while (iterator.hasNext()) {
			// Check, if the good already exists in the good pool
			String key = (String) iterator.next();
			Good good = market.getEnvironment().getGoodPool().getGood(key);
			if (good == null) {
				good = new Good(key);
				for (int a = 0; a < MarketConstansts.getInstance().getNumberOfResourceAttributes(); a++) {
					Attribute attribute = new Attribute("a" + a);
					if (!market.getEnvironment().getAttributePool().contains(attribute)) {
						market.getEnvironment().getAttributePool().addAttribute(attribute);
					} else {
						attribute = market.getEnvironment().getAttributePool().getAttribute(
							attribute);
					}
					good.addAttribute(attribute);
				}
				market.getEnvironment().getGoodPool().addGood(good);
			}
			bundle.add(good);
		}
		bundle.setName(bundle.getId());
		// Check, if this bundle already exists ...
		if (market.getEnvironment().getBundlePool().contains(bundle))
			bundle = market.getEnvironment().getBundlePool().getBundle(bundle);
		else
			market.getEnvironment().getBundlePool().addBundle(bundle);
		return bundle;
	}

	/**
	 * Generates an attribute characteristics object for a given bundle and a given transaction
	 * object
	 * 
	 * @param bundle
	 *            bundle
	 * @param negotationObject
	 *            Transaction object
	 * @return Bundle
	 */
	private AttributesBundle getAttributesBundle(Bundle bundle, HashMap negotationObject)
		throws MaceException {
		AttributesBundle attributesBundle = new AttributesBundle();
		for (int a = 0; a < bundle.size(); a++) {
			Good good = bundle.getGood(a);
			for (int b = 0; b < MarketConstansts.getInstance().getNumberOfResourceAttributes(); b++) {
				attributesBundle.addQuality(good, good.getAttribute(b).getId(),
					((Integer) negotationObject.get(good.getId())).doubleValue());
			}
		}
		return attributesBundle;
	}

	private void informAgents() {

		Outcome outcome = market.getOutcome();

		// Start to notify the buyers
		Iterator buyerAllocationIterator = outcome.getAllocationBuyerBeanPool().getValueIterator();
		while (buyerAllocationIterator.hasNext()) {
			OutcomeBuyerBean buyerBean = (OutcomeBuyerBean) buyerAllocationIterator.next();
			Negotiation negotiation = ((CATNETSAgent) buyerBean.getAgent()).getNegotiation();
			Negotiator buyAgent = negotiation.getNegotiator();
			double price = buyerBean.getPrice();
			log.debug("Trade Buyer " + buyAgent.toString() + ",  price: " + price + ", bid: "
				+ buyerBean.getBid() + " transaction object: " + buyerBean.getBundle().getId()
				+ " negotiation: " + negotiation + "\n");
			// Pseuedo-Satisfaction (1-price/valuation)
			double value = buyerBean.getPrice() / buyerBean.getBid();
			MetricsLogger.instance().logMetric(_time.getRunningTimeMillis(),
				negotiation + " " + buyAgent, "util_satisfaction_buyer_resource_central",
				"" + value + " success");

			// Search for the corresponding sellers
			ArrayList sellers = outcome.getAllocationSellerBeanPool().getAllocatedBeans(
				buyerBean.getAgent());
			// Inform agent
			Vector<Negotiation> sellerNegos = new Vector<Negotiation>();
			for (int a = 0; a < sellers.size(); a++) {
				// Seller negotiator
				Negotiation sellerNego = ((CATNETSAgent) ((OutcomeSellerBean) sellers.get(a))
					.getAgent()).getNegotiation();
				sellerNegos.add(sellerNego);
				log.debug("Trade Buyer Allocated by seller : " + a + " "
					+ ((CATNETSAgent) ((OutcomeSellerBean) sellers.get(a)).getAgent()).getId());
				MetricsLogger.instance().logMetric(_time.getRunningTimeMillis(),
					buyAgent + " " + sellerNego, "accepts", "" + negotiation);

				MetricsLogger.instance().logMetric(_time.getRunningTimeMillis(), "" + negotiation,
					"trade_resource_central", "" + sellerNego);

			}
			// Notify the buyer
			double percentage = 1;
			CentralisedPoint2PointMessage retMsg = new CentralisedPoint2PointMessage(negotiation,
				((Negotiation) sellerNegos.get(0)).getNegotiator(), buyAgent, null, price,
				percentage, sellerNegos);
			_p2pMediator.acceptMessage(retMsg);
		}

		// ... Notify the sellers
		Iterator sellerAllocationIterator = outcome.getAllocationSellerBeanPool()
			.getValueIterator();
		while (sellerAllocationIterator.hasNext()) {
			OutcomeSellerBean sellerBean = (OutcomeSellerBean) sellerAllocationIterator.next();
			Negotiation negotiation = ((CATNETSAgent) sellerBean.getAgent()).getNegotiation();
			Negotiator sellAgent = negotiation.getNegotiator();
			ArrayList allocations = sellerBean.getAllocation(1);
			double price = sellerBean.getPrice();
			Vector<Negotiation> buyerNegos = new Vector<Negotiation>();
			double percentage = 0;
			for (int a = 0; a < allocations.size(); a++) {
				OutcomeSellerSlotBean slotBean = (OutcomeSellerSlotBean) allocations.get(a);
				percentage = slotBean.getPercentage();
				log.debug("Trade Seller " + sellAgent.toString() + ",  price: "
					+ sellerBean.getPrice() + ", bid: " + sellerBean.getBid()
					+ " transaction object: " + sellerBean.getBundle().getId() + " negotiation: "
					+ negotiation + " percentage: " + percentage);
				buyerNegos.add(((CATNETSAgent) slotBean.getBuyerAgent()).getNegotiation());
				double value = 1 - sellerBean.getBid() / sellerBean.getPrice();
				// double value = 1
				// - (sellerBean.getPrice() - sellerBean.getBid())
				// / sellerBean.getBid();
				MetricsLogger.instance().logMetric(_time.getRunningTimeMillis(),
					negotiation + " " + sellAgent, "util_satisfaction_seller_resource_central",
					"" + value + " success");
			}
			CentralisedPoint2PointMessage retMsg = new CentralisedPoint2PointMessage(negotiation,
				this, sellAgent, null, price, percentage, buyerNegos);
			_p2pMediator.acceptMessage(retMsg);
		}

	}

	private void updateMarketPrices() {
		// marketPrices = new HashMap<String, Double>();
		for (int a = 0; a < market.getEnvironment().getBundlePool().size(); a++) {
			Bundle bundle = market.getEnvironment().getBundlePool().getBundle(a);
			double price = 0;
			AbstractOutcomeBean buyerBean = market.getOutcome().getHighestSuccessfullBuyerPrice(
				bundle);

			if (buyerBean != null) {
				price = buyerBean.getPrice();
			}
			if (price > 0) {
				marketPrices.put(bundle.toString(), new Double(price));
				MetricsLogger.instance().logMetric(_time.getRunningTimeMillis(), this.getName(),
					"market_price_resource_central", "" + bundle.getId() + " " + price);
			}
		}
	}

	/**
	 * The RMAA waits until it receives a message
	 * 
	 * @param delay
	 *            Milliseconds to wait
	 */
	private void standBy(long delay) {
		if (_clearingPolicy.equals(MarketConstansts.CallA)) {
			// _time.gtWait(this, delay);
			_time.gtSleep(delay);
		}

		synchronized (_messageQueue) {
			// If there are messages to process, return immediately.
			if (_messageQueue.size() != 0)
				return;
		}
		if (_clearingPolicy.equals(MarketConstansts.CDA))
			_time.gtWait(this);
	}

	public synchronized void shutDownRMAA() {
		_iAmAlive = false;
		_time.gtNotify(this);
	}

	public synchronized void addMessage(Message msg) {
		synchronized (_messageQueue) {
			_messageQueue.add(msg);
		}
		_time.gtNotify(this);
	}

	public String toString() {
		return getName();
	}

	/** ******* Get methods from the interface ********** */
	public AlnSite getSite() {
		return AlnContainer.getInstance().getRCASite();
	}

	/**
	 * Function to retrieve the last transaction price for a given service type !!TODO This is a
	 * work around. This should be replaced by a message
	 * 
	 * @param bsName
	 *            Service type
	 * @return Last transaction type
	 */

	public static double getLastPrice(HashMap bsName) {
		Iterator iterator = bsName.keySet().iterator();
		String newKey = "";
		while (iterator.hasNext()) {
			newKey += iterator.next() + Bundle.GOOD_SEPARATOR;
		}
		if (newKey.endsWith(Bundle.GOOD_SEPARATOR)) {
			newKey = newKey.substring(0, newKey.length() - 1);
		}
		return computeMarketPriceValue(newKey);
	}

	/**
	 * Function to retrieve the last transaction price for a given service type !!TODO This is a
	 * work around. This should be replaced by a message
	 * 
	 * @param bundle
	 *            Service type
	 * @return Last transaction type
	 */

	public static double getLastPrice(Bundle bundle) {
		return computeMarketPriceValue(bundle.toString());
	}

	/**
	 * Function to retrieve the last transaction price for a given service type !!TODO This is a
	 * work around. This should be replaced by a message
	 * 
	 * @param newKey
	 *            with the key
	 * @return Last transaction type
	 */
	private static double computeMarketPriceValue(String newKey) {
		if (marketPrices.containsKey(newKey)) {
			return marketPrices.get(newKey).doubleValue();
		} else {
			// If there is no market price in the keyset, let's see if there
			// is a price for a similar bundle.
			// In this case, similar bundle means "subsetOf" bundle
			double price = 0;
			for (Iterator keyIterator = marketPrices.keySet().iterator(); keyIterator.hasNext();) {
				String tempKey = (String) keyIterator.next();
				if (newKey.lastIndexOf(tempKey) > 0) {
					double tempPrice = marketPrices.get(tempKey).doubleValue();
					if (price < tempPrice)
						price = tempPrice;
				}
			}

			// log.warn("No market price found for " + newKey
			// + ". I am generating one...");
			if (price == 0)
				price = ExtendedRandomSingleton.instance().nextDouble() * 10;
			marketPrices.put(newKey, price);
			return price;
		}

	}

	private CATNETSAgent getAgentFromPool(AbstractAgentPool agentPool, Negotiation negotiation) {
		for (int a = 0; a < agentPool.size(); a++) {
			CATNETSAgent agent = (CATNETSAgent) agentPool.getAgent(a);
			if (negotiation.toString().equals(agent.getId()))
				return agent;
		}
		return null;
	}
}
