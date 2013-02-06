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
 * Represents a pool storing Bundles
 * 
 * Created: 20.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class BundlePool extends EntityPool implements XMLExporter {

	private static final long serialVersionUID = 1251499631669867683L;

	/**
	 * @param id
	 *            Id value of the requested bundle
	 * @return The requested bundle instance
	 */
	public Bundle getBundle(String id) {
		return (Bundle) getEntity(id);
	}

	/**
	 * @param bundle
	 *            A bundle instance of the requested bundle
	 * @return The requested bundle instance
	 */
	public Bundle getBundle(Bundle bundle) {
		return (Bundle) getEntity(bundle);
	}

	/**
	 * @param index
	 *            Index value of the requested bundle
	 * @return The requested bundle instance
	 */
	public Bundle getBundle(int index) {
		return (Bundle) getEntity(index);
	}

	/**
	 * Parses the pool for a given bundle name
	 * 
	 * @param name
	 *            Name to search for
	 * @return Bundle
	 */
	public Bundle getBundleByName(String name) {
		Iterator iterator = getValueIterator();
		while (iterator.hasNext()) {
			Bundle bundle = (Bundle) iterator.next();
			if (bundle.getName().equals(name)) {
				return bundle;
			}
		}
		return null;
	}

	/**
	 * Searches for a given bundle by means of comparing all goods inside
	 * 
	 * @param bundle
	 *            Bundle to search for
	 * @return Bundle
	 */
	public Bundle getBundleByGood(Bundle bundle) {
		for (int a = 0; a < size(); a++) {
			Bundle toCompare = getBundle(a);
			if (toCompare.equalsByGoodNames(bundle)) {
				return toCompare;
			}
		}
		return null;
	}

	/**
	 * Returns all bundles which contain a given good
	 * 
	 * @param good
	 *            Good to search
	 * @return BundlePool which includes all relevant bundle instances
	 */
	public BundlePool getAllBundles(Good good) throws MaceException {
		BundlePool result = new BundlePool();
		int size = size();
		Bundle bundle = null;
		for (int a = 0; a < size; a++) {
			bundle = getBundle(a);
			if (bundle.contains(good)) {
				result.addBundle(bundle);
			}
		}
		return result;
	}

	/**
	 * Adds a bundle to the pool
	 * 
	 * @param bundle
	 *            Bundle to add
	 */
	public void addBundle(Bundle bundle) throws MaceException {
		addEntity(bundle);
	}

	/**
	 * Clones a bundle pool as well as the containing bundles The goods are not
	 * cloned
	 * 
	 * @return Cloned Good Pool
	 */
	public BundlePool cloneBundlePool() throws MaceException {
		BundlePool result = new BundlePool();
		Iterator bundles = getValueIterator();
		while (bundles.hasNext()) {
			Bundle bundleToClone = (Bundle) bundles.next();
			Bundle clonedBundle = bundleToClone.cloneBundle();
			result.addBundle(clonedBundle);
		}
		return result;
	}

	/**
	 * Clones a bundle pool as well as the containing bundles The goods will be
	 * selected from the goodPool
	 * 
	 * @param goodPool
	 *            The goodpool containing the new references
	 * @return Cloned Good Pool
	 */
	@Deprecated
	public BundlePool cloneBundlePool(GoodPool goodPool) throws MaceException{
		BundlePool result = new BundlePool();
		Iterator bundles = getValueIterator();
		while (bundles.hasNext()) {
			Bundle clonedBundle = ((Bundle) bundles.next())
					.cloneBundle(goodPool);
			result.addBundle(clonedBundle);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.XMLExporter#toXML(org.w3c.dom.Document)
	 */
	public Element toXML(Document document) {
		Iterator iterator = getValueIterator();
		Element bundles = document.createElement("bundles");
		while (iterator.hasNext()) {
			Bundle bundle = (Bundle) iterator.next();
			Element bundleElement = bundle.toXML(document);
			bundles.appendChild(bundleElement);
			Iterator goodIterator = bundle.getGoodIterator();
			while (goodIterator.hasNext()) {
				Good goodElement = (Good) goodIterator.next();
				bundleElement.appendChild(goodElement.toXML(document));
			}
		}
		return bundles;
	}

}
