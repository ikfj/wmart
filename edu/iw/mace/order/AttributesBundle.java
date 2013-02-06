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

import java.util.*;

import edu.iw.mace.environment.Good;
import edu.iw.mace.environment.MaceException;

/**
 * Represents attribute characteristics for a bundle
 *
 * Created: 20.08.2004
 *
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */

public class AttributesBundle extends HashMap<Good, AttributesGood> {

	private static final long serialVersionUID = -4475736167913280514L;

	/**
	 * Adds a quality characteristic for a given good and attribute
	 *
	 * @param good
	 *            The selected good
	 * @param attributeID
	 *            The id of the target attribute
	 * @param value
	 *            The attribute value
	 */
	public void addQuality(Good good, String attributeID, double value)
			throws MaceException {
		if (!good.containsAttribute(attributeID))
			throw new MaceException("Good " + good.getId()
					+ " does not contain the attribute " + attributeID);
		AttributesGood goodQuality = getGood(good);
		if (goodQuality == null) {
			goodQuality = new AttributesGood();
			put(good, goodQuality);
		}

		goodQuality.addAttribute(attributeID, value);
	}

	/**
	 * Adds a quality set for a given good
	 *
	 * @param good
	 *            The selected good
	 * @param attributes
	 *            The quality set
	 */
	public void addQuality(Good good, AttributesGood attributes) {
		put(good, attributes);
	}

	/**
	 * Returns the attribute characteristics for a given good
	 *
	 * @param good
	 *            Good to look for
	 * @return Attribute characteristics
	 */
	public AttributesGood getGood(Good good) {
		if (containsKey(good)) {
			return get(good);
		} else {
			return null;
		}
	}

	/**
	 * Returns the quality characteristic for a given good and attribute
	 *
	 * @param good
	 *            Given good
	 * @param attributeId
	 *            Given attribute Id
	 * @return Quality value
	 */
	public double getQuality(Good good, String attributeId) {
		AttributesGood goodQuality = getGood(good);
		if (goodQuality == null) {
			return 0;
		}
		return goodQuality.getAttributeValue(attributeId);
	}

}
