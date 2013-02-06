/*
 * jCase - Java Combinatorial Auction Simulator 
 * Copyright (C) 2004-2006 Bjoern Schnizler, University of Karlsruhe (TH)
 * http://www.iw.uni-karlsruhe.de/jcase
 *
 * Parts of this work are funded by the European Union
 * under the IST project CATNETS (http://www.catnets.org/)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or any later version.
 * See the GNU General Public License for more details.
 *
 * This code comes WITHOUT ANY WARRANTY
 */

package edu.iw.mace.environment;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iw.utils.XMLUtils;

/**
 * Environment Management
 * 
 * Created: 20.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class Environment {

	private GoodPool goodPool = null;

	private BundlePool bundlePool = null;

	private BuyerAgentPool buyerPool = null;

	private SellerAgentPool sellerPool = null;

	private AttributePool attributePool = null;

	private TimeRange timeRange = null;

	private static Logger log = Logger.getLogger(Environment.class);

	/**
	 * Empty constructor of the environment
	 */
	public Environment() {
		resetEnvironment();
	}

	/**
	 * Reset the environment Clears all pools
	 */
	public void resetEnvironment() {
		goodPool = new GoodPool();
		bundlePool = new BundlePool();
		buyerPool = new BuyerAgentPool();
		sellerPool = new SellerAgentPool();
		attributePool = new AttributePool();
		timeRange = new TimeRange();
	}

	/**
	 * Loads an environment from an XML nodelist
	 * 
	 * @param nodeList
	 *            NodeList containing environment information
	 */
	public void load(NodeList nodeList) throws MaceException {
		// Reset the environment
		resetEnvironment();

		for (int x = 0; x < nodeList.getLength(); x++) {
			Node node = nodeList.item(x);
			if (node.getNodeName().equals("goods")) {
				NodeList childs = node.getChildNodes();
				for (int y = 0; y < childs.getLength(); y++) {
					Node child = childs.item(y);
					if (child.getNodeName().equals("good")) {
						String id = XMLUtils.getAttributeValue(child, "id");
						log.debug("New good: " + id);
						Good good = new Good(id);
						goodPool.addGood(good);
						NodeList attributeList = child.getChildNodes();
						for (int a = 0; a < attributeList.getLength(); a++) {
							Node concreteNode = attributeList.item(a);
							if (concreteNode.getNodeName().equals("attribute")) {
								String attributeId = XMLUtils.getAttributeValue(concreteNode, "id");
								Attribute attribute = getAttributePool().createNewAttribute(
										attributeId);
								if (attribute == null) {
									log.error("Could not find attribute " + attribute.getId()
											+ " in pool");
								}
								good.addAttribute(attribute);
								log.debug("New attribute for good " + id + " : " + attributeId);
							}
						}
					}
				}
			} else if (node.getNodeName().equals("bundles")) {
				NodeList childs = node.getChildNodes();
				for (int y = 0; y < childs.getLength(); y++) {
					Node child = childs.item(y);
					if (child.getNodeName().equals("bundle")) {
						String id = XMLUtils.getAttributeValue(child, "id");
						log.debug("New bundle: " + id);
						Bundle bundle = new Bundle();
						bundle.setName(id);
						bundle.setId(id);
						// Goods
						NodeList goods = child.getChildNodes();
						for (int z = 0; z < goods.getLength(); z++) {
							Node goodNode = goods.item(z);
							if (goodNode.getNodeName().equals("good")) {
								String goodID = XMLUtils.getAttributeValue(goodNode, "id");
								Good goodAdd = goodPool.getGood(goodID);
								if (goodAdd != null) {
									bundle.add(goodAdd);
								} else {
									log.error("Trying to add Good to Bundle. Could not find Good "
											+ goodID + " in pool.");
								}
							}
						}
						bundlePool.addBundle(bundle);
					}
				}
			} else if (node.getNodeName().equals("attributes")) {
				NodeList childs = node.getChildNodes();
				for (int y = 0; y < childs.getLength(); y++) {
					Node child = childs.item(y);
					if (child.getNodeName().equals("attribute")) {
						String attributeID = XMLUtils.getAttributeValue(child, "id");
						log.debug("New Attribute: " + attributeID);
						attributePool.createNewAttribute(attributeID);
					}
				}
			}
		}
	}

	/**
	 * Export the environment to XML
	 * 
	 * @param document
	 *            XML document node
	 * @param environmentTag
	 *            Existing element tag for the environment
	 */
	public void toXML(Document document, Element environmentTag) {

		// Environment
		environmentTag.appendChild(goodPool.toXML(document));
		environmentTag.appendChild(bundlePool.toXML(document));
		environmentTag.appendChild(attributePool.toXML(document));
	}

	/**
	 * @return Returns the number of bundles
	 */
	public int getNumberBundles() {
		return bundlePool.size();
	}

	/**
	 * @return Returns the bundepool
	 */
	public BundlePool getBundlePool() {
		return bundlePool;
	}

	/**
	 * Sets the bundlePool
	 * 
	 * @param bundlePool
	 *            to set
	 */
	public void setBundlePool(BundlePool bundlePool) {
		this.bundlePool = bundlePool;
	}

	/**
	 * @return the BuyerAgentPool
	 */
	public BuyerAgentPool getBuyerPool() {
		return buyerPool;
	}

	/**
	 * @param buyerPool
	 */
	public void setBuyerPool(BuyerAgentPool buyerPool) {
		this.buyerPool = buyerPool;
	}

	/**
	 * @return the GoodPool
	 */
	public GoodPool getGoodPool() {
		return goodPool;
	}

	/**
	 * @param goodPool
	 */
	public void setGoodPool(GoodPool goodPool) {
		this.goodPool = goodPool;
	}

	/**
	 * @return the SellerPool
	 */
	public SellerAgentPool getSellerPool() {
		return sellerPool;
	}

	/**
	 * @param sellerPool
	 */
	public void setSellerPool(SellerAgentPool sellerPool) {
		this.sellerPool = sellerPool;
	}

	/**
	 * @return the AttributePool
	 */
	public AttributePool getAttributePool() {
		return attributePool;
	}

	/**
	 * @param attributePool
	 */
	public void setAttributePool(AttributePool attributePool) {
		this.attributePool = attributePool;
	}

	/**
	 * @return the timeRange
	 */
	public TimeRange getTimeRange() {
		return timeRange;
	}

	/**
	 * @param timeRange
	 *            the timeRange to set
	 */
	public void setTimeRange(TimeRange timeRange) {
		this.timeRange = timeRange;
	}

	@Override
	public String toString() {
		return "Environment" + "{ goodPool=" + getGoodPool() + ", bundlePool=" + getBundlePool()
				+ ", buyerPool=" + getBuyerPool() + ", sellerPool=" + getSellerPool()
				+ ", attributePool=" + getAttributePool() + ", timeRange=" + getTimeRange() + " }";
	}

	public static void main(String args[]) {
		Environment environment = new Environment();
		System.out.println(environment);
	}

}