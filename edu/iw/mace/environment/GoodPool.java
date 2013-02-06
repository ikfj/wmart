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

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iw.utils.XMLExporter;
import edu.iw.utils.patterns.EntityPool;

/**
 * Stores and manages the goods
 * 
 * Created: 20.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class GoodPool extends EntityPool implements XMLExporter {

	private static final long serialVersionUID = 5289039843822080218L;

	/**
	 * @param id
	 *            The id of the requested good
	 * @return Good
	 */
	public Good getGood(String id) {
		return (Good) getEntity(id);
	}

	/**
	 * @param index
	 *            The index of the requested good
	 * @return Good
	 */
	public Good getGood(int index) {
		return (Good) getEntity(index);
	}

	/**
	 * @param g
	 *            Adds the good to the pool
	 */
	public void addGood(Good g) throws MaceException{
		addEntity(g);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "";
		for (int a = 0; a < size(); a++) {
			result += getGood(a).getId();
			if (a < size() - 1) {
				result += ";";
			}
		}
		return result;
	}

	/**
	 * Clones a good pool as well as the containing goods
	 * 
	 * @return Cloned Good Pool
	 */
	public GoodPool cloneGoodPool() throws MaceException {
		GoodPool result = new GoodPool();
		Iterator goods = getValueIterator();
		while (goods.hasNext()) {
			Good clonedGood = ((Good) goods.next()).cloneGood();
			result.addGood(clonedGood);
		}
		return result;
	}

	/**
	 * Checks if all goods inside the given bundle are stored in the pool.
	 * 
	 * @param bundle
	 *            The bundle containing the goods
	 * @return <true> if all goods are represented by the pool, <false> if not
	 */
	public boolean containsAllGoods(Bundle bundle) {
		for (int a = 0; a < bundle.size(); a++) {
			if (!contains(bundle.getGood(a))) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.XMLExporter#toXML(org.w3c.dom.Document)
	 */
	public Element toXML(Document document) {
		Iterator iterator = getValueIterator();
		Element goods = document.createElement("goods");
		while (iterator.hasNext()) {
			Good good = (Good) iterator.next();
			Element goodElement = good.toXML(document);
			for (int a = 0; a < good.getNumberAttributes(); a++) {
				goodElement.appendChild(good.getAttribute(a).toXML(document));
			}
			goods.appendChild(goodElement);
		}
		return goods;
	}

}
