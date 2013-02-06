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
package edu.iw.mace.order.splitting;

import java.util.Iterator;

import edu.iw.mace.environment.Bundle;
import edu.iw.mace.environment.GoodPool;
import edu.iw.mace.environment.MaceException;
import edu.iw.utils.patterns.EntityPool;

/**
 * Manages a set of nodes
 * 
 * Created 15.11.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 */
public class NodePool extends EntityPool {

	private static final long serialVersionUID = 8258331848243282490L;

	/**
	 * Adds a node to the nodepool
	 * 
	 * @param node
	 *            Node to add
	 */
	public void addNode(Node node) throws MaceException {
		addEntity(node);
	}

	/**
	 * Search for a node with a given index value
	 * 
	 * @param index
	 *            The index value of the requested node
	 * @return node The node instance
	 */
	public Node getNode(int index) {
		return (Node) getEntity(index);
	}

	/**
	 * Search for a node with a given id value
	 * 
	 * @param id
	 *            The id value of the requested node
	 * @return node The node instance
	 */
	public Node getNode(String id) {
		return (Node) getEntity(id);
	}

	/**
	 * Checks if all goods inside the given bundle are stored as nodes in the
	 * nodepool.
	 * 
	 * @param bundle
	 *            The bundle containing the goods
	 * @return <true> if all goods are represented by the pool, <false> if not
	 */
	public boolean containsAllGoods(Bundle bundle) {
		for (int a = 0; a < bundle.size(); a++) {
			if (!contains(bundle.getGood(a).getId())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Computes the number of goods of a bundle contained in the nodepool
	 * 
	 * @param bundle
	 *            The bundle containing the goods
	 * @return Number of goods in the bundle
	 */
	public int getNumberContainedGoods(Bundle bundle) {
		int result = 0;
		for (int a = 0; a < bundle.size(); a++) {
			if (contains(bundle.getGood(a).getId())) {
				result++;
			}
		}
		return result;
	}

	/**
	 * Returns a goodpool containing all goods of a bundle contained in the
	 * nodepool
	 * 
	 * @param bundle
	 *            The bundle containing the goods
	 * @return Goodpool including the relevant goods
	 */
	public GoodPool getGoodsContainedInPool(Bundle bundle) throws MaceException {
		GoodPool result = new GoodPool();
		for (int a = 0; a < bundle.size(); a++) {
			if (contains(bundle.getGood(a).getId())) {
				result.addGood(bundle.getGood(a));
			}
		}
		return result;
	}

	/**
	 * Computes the sum of all node's weights in the pool
	 * 
	 * @return Sum of the node's weights
	 */
	public double computeWeight() {
		double value = 0.0;
		Iterator iterator = getValueIterator();
		while (iterator.hasNext()) {
			Node node = (Node) iterator.next();
			value += node.getWeigth();
		}
		return value;
	}

	/**
	 * Returns the maximum weight of all nodes in the pool
	 * 
	 * @return Maximum weight
	 */
	public double getMaxNodeWeight() {
		double value = 0.0;
		Iterator iterator = getValueIterator();
		while (iterator.hasNext()) {
			Node node = (Node) iterator.next();
			if (node.getWeigth() > value) {
				value = node.getWeigth();
			}
		}
		return value;
	}


	/**
	 * Selects the unlocked node with the highest gain value in the pool
	 * 
	 * @return Node which is unlocked and has the highest Weight in the pool
	 */
	public Node getUnlockedNodeWithHighestGain() {
		double gain = 0;
		boolean first = true;
		Node result = null;
		Iterator iterator = getValueIterator();
		while (iterator.hasNext()) {
			Node node = (Node) iterator.next();
			if (!node.isLocked()) {
				if (first) {
					gain = node.getGainValue();
					result = node;
					first = false;
				} else {
					if (node.getGainValue() > gain) {
						gain = node.getGainValue();
						result = node;
					}
				}
			}
		}
		if (first)
			return null;
		return result;
	}

}
