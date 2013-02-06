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

import org.w3c.dom.Element;

import edu.iw.utils.XMLExporter;
import edu.iw.utils.patterns.Entity;

/**
 * Represents a bundle
 * 
 * Created: 20.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */

public class Bundle extends Entity implements XMLExporter, Cloneable {

	public static final String GOOD_SEPARATOR=";";
	
	/**
	 * Stores a set of Goods of the bundle
	 */
	private GoodPool goods = null;

	private String name = "";

	/**
	 * Empty constructor
	 */
	public Bundle() {
		goods = new GoodPool();
	}

	/**
	 * @param index
	 * @return The Good whit the given index
	 */
	public Good getGood(int index) {
		return (Good) goods.getEntity(index);
	}

	/**
	 * @param id
	 * @return The Good whit the given id
	 */
	public Good getGood(String id) {
		return (Good) goods.getEntity(id);
	}

	/**
	 * Removes a good
	 * 
	 * @param id
	 *            The id of the good to be removed
	 */
	public void removeGood(String id) {
		goods.removeEntity(id);
	}

	/**
	 * @param good
	 */
	public void add(Good good) throws MaceException {
		goods.addGood(good);
		this.setId(toString());
	}

	/**
	 * Returns the "string" of the bundle. For instance the method returns "abc"
	 * if the bundle contrains the goods "a", "b", and "c".
	 */
	@Override
	public String toString() {
		String result = "";
		for (int a = 0; a < goods.size(); a++) {
			result += getGood(a).getId();
			if (a < goods.size() - 1) {
				result += ";";
			}
		}
		return result;
	}

	/**
	 * Checks, if a good is part of the bundle
	 * 
	 * @param good
	 *            Good to search for
	 * @return <true> if the good is part of the bundle, <false> if not
	 */
	public boolean contains(Good good) {
		return goods.contains(good.getId());
	}

	/**
	 * The size of a bundle (number of goods inside)
	 * 
	 * @return Size of a bundle
	 */
	public int size() {
		return goods.size();
	}

	/**
	 * Returns an iterator of all goods in the bundle
	 * 
	 * @return Returns an iterator of all goods in the bundle
	 */
	public Iterator getGoodIterator() {
		return goods.getValueIterator();
	}

	/**
	 * Compares a bundle by comparing the good names (and not the id's)
	 * 
	 * @param bundle
	 *            Bundle to compare
	 * @return <true> if the bundles are semantically equal, <false> if the
	 *         bundles are not equal
	 */
	public boolean equalsByGoodNames(Bundle bundle) {
		return bundle.toString().equals(toString());
	}

	/**
	 * Clones a bundle The goods are not cloned
	 * 
	 * @return A new instance (clone) of the bundle with an id "cloned"
	 */
	public Bundle cloneBundle() throws MaceException{
		Bundle result = new Bundle();
		// Copy all goods into the new bundle
		int size = goods.size();
		for (int a = 0; a < size; a++) {
			result.add(getGood(a));
		}
		result.setName(getName());
		return result;
	}

	/**
	 * Clones a bundle. The references for the new goods are taken from the
	 * goodpool
	 * 
	 * @param goodPool
	 *            The new good pool
	 * @return A new instance (clone) of the bundle with an id "cloned"
	 */
	public Bundle cloneBundle(GoodPool goodPool) throws MaceException {
		Bundle result = new Bundle();
		// Copy all goods into the new bundle
		for (int a = 0; a < goods.size(); a++) {
			Good good = getGood(a);
			result.add(goodPool.getGood(good.getId()));
		}
		return result;
	}

	/**
	 * Checks if this bundle is a subset of the given bundle
	 * 
	 * @param bundle
	 *            Bundle to compare
	 * @return <true> if the bundle is a subset, <false> otherwise
	 */
	public boolean isSubSetOf(Bundle bundle) {
		boolean result = true;
		for (int a = 0; a < size(); a++) {
			if (!bundle.contains(getGood(a))) {
				result = false;
			}
		}
		return result;
	}

	/**
	 * Checks if the union of two bundles is empty or not, i.e. if they share
	 * same goods
	 * 
	 * @param bundle
	 * @return <true> if the union is not empty, <false> is the union is empty
	 */
	public boolean unionNotEmpty(Bundle bundle) {
		for (int a = 0; a < size(); a++) {
			if (bundle.contains(getGood(a))) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.XMLExporter#toXML(org.w3c.dom.Document)
	 */
	public Element toXML(org.w3c.dom.Document document) {
		Element element = document.createElement("bundle");
		element.setAttribute("id", getName());
		return element;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

}
