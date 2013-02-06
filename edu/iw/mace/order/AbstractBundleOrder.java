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

package edu.iw.mace.order;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iw.mace.environment.Bundle;
import edu.iw.mace.environment.Good;
import edu.iw.mace.environment.MaceException;
import edu.iw.utils.XMLExporter;
import edu.iw.utils.patterns.Entity;

/**
 * An abstract bundle order represents an atomic bid on a specifc bundle. Bundle
 * orders are part of orders.
 * 
 * Created: 20.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public abstract class AbstractBundleOrder extends Entity implements
		XMLExporter {

	private float valuation;

	private float bid;

	private int early;

	private int latest;

	private Bundle bundle;

	private AttributesBundle quality;

	private AbstractOrder abstractOrder;

	protected NumberFormat df = null;

	/**
	 * Constructor
	 * 
	 * @param abstractOrder
	 *            Parent order of this bundle order
	 */
	public AbstractBundleOrder(AbstractOrder abstractOrder) throws MaceException {
		super();
		generateNewId();
		this.quality = new AttributesBundle();
		this.abstractOrder = abstractOrder;
		df = NumberFormat.getInstance(Locale.ENGLISH);
		df.setMaximumFractionDigits(6);
	}

	/**
	 * Adds quality value to a given good and attribute
	 * 
	 * @param good
	 *            Given good
	 * @param attributeId
	 *            Given attribute Id
	 * @param qualityValue
	 *            Quality values
	 */
	public void addQuality(Good good, String attributeId, double qualityValue)
			throws MaceException {
		if (!bundle.contains(good))
			throw new MaceException("Bundle " + bundle.getId()
					+ " does not contain the resource " + good.getId());
		if (!good.containsAttribute(attributeId))
			throw new MaceException("Good " + good.getId()
					+ " does not contain the attribute " + attributeId);
		quality.addQuality(good, attributeId, qualityValue);
	}

	/**
	 * Exports specific buyer/seller parameter to XML
	 * 
	 * @param document
	 *            Document root
	 * @return Element containing information
	 */
	public abstract Element toXMLSpecific(Document document);

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.XMLExporter#toXML(org.w3c.dom.Document)
	 */
	public Element toXML(Document document) {
		Element element = document.createElement("bundle");
		element.setAttribute("id", bundle.getName());

		// Price of an order is the bid
		Element priceElement = document.createElement("price");
		priceElement.setAttribute("value", String.valueOf(df.format(bid)));
		element.appendChild(priceElement);

		Element earlyElement = document.createElement("early");
		earlyElement.setAttribute("value", String.valueOf(early));
		element.appendChild(earlyElement);

		Element latestElement = document.createElement("latest");
		latestElement.setAttribute("value", String.valueOf(latest));
		element.appendChild(latestElement);

		Element qualityElement = document.createElement("quality");
		Iterator iterator = quality.keySet().iterator();
		while (iterator.hasNext()) {
			Good good = (Good) iterator.next();
			Element goodElement = good.toXML(document);
			for (int a = 0; a < good.getNumberAttributes(); a++) {
				String attributeID = good.getAttribute(a).getId();
				int goodQuality = (int) quality.getQuality(good, attributeID);
				Element attribute = good.getAttribute(a).toXML(document);
				attribute.appendChild(document.createTextNode(String
						.valueOf(goodQuality)));
				goodElement.appendChild(attribute);
			}
			qualityElement.appendChild(goodElement);

		}
		element.appendChild(qualityElement);

		Element specific = toXMLSpecific(document);
		if (specific != null) {
			element.appendChild(specific);
		}
		return element;
	}

	/**
	 * @return Returns the early.
	 */
	public int getEarly() {
		return early;
	}

	/**
	 * @param early
	 *            The early to set.
	 */
	public void setEarly(int early) {
		this.early = early;
	}

	/**
	 * @return Returns the latest.
	 */
	public int getLatest() {
		return latest;
	}

	/**
	 * @param latest
	 *            The latest to set.
	 */
	public void setLatest(int latest) {
		this.latest = latest;
	}

	/**
	 * @return Returns the quality.
	 */
	public AttributesBundle getQuality() {
		return quality;
	}

	/**
	 * @param quality
	 *            The quality to set.
	 */
	public void setQuality(AttributesBundle quality) {
		this.quality = quality;
	}

	/**
	 * @return Returns the bundle.
	 */
	public Bundle getBundle() {
		return bundle;
	}

	/**
	 * @param bundle
	 *            The bundle to set.
	 */
	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * @return Returns the abstractOrder.
	 */
	public AbstractOrder getAbstractOrder() {
		return abstractOrder;
	}

	/**
	 * @param abstractOrder
	 *            The abstractOrder to set.
	 */
	public void setAbstractOrder(AbstractOrder abstractOrder) {
		this.abstractOrder = abstractOrder;
	}

	/**
	 * @return Returns the bid.
	 */
	public double getBid() {
		return bid;
	}

	/**
	 * @param bid
	 *            The bid to set.
	 */
	public void setBid(double bid) {
		this.bid = (float) bid;
	}

	/**
	 * @return Returns the valuation.
	 */
	public double getValuation() {
		return valuation;
	}

	/**
	 * @param valuation
	 *            The valuation to set.
	 */
	public void setValuation(double valuation) {
		this.valuation = (float) valuation;
	}

}
