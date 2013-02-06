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

import edu.iw.mace.environment.Good;
import edu.iw.utils.patterns.Entity;

/**
 * This class represents a Node. A node basically represents a good in the
 * market.
 * 
 * Created 04.11.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 */
public class Node extends Entity {

	private float weigth;

	private float gainValue;

	private boolean locked = false;

	private boolean partOfLeftSide = true;

	private Good good = null;

	/**
	 * Constructor
	 * 
	 * @param good
	 *            good ID of the node
	 * @param weigth
	 *            Initial weight of the node
	 */
	public Node(Good good, double weigth) {
		super(good.getId());
		this.good = good;
		this.weigth = (float) weigth;
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            ID of the node
	 * @param weigth
	 *            Initial weight of the node
	 */
	public Node(String id, double weigth) {
		super(id);
		this.weigth = (float) weigth;
	}

	/**
	 * Adds a value to the current weight of this node
	 * 
	 * @param value
	 *            Value to add
	 */
	public void addWeight(double value) {
		this.weigth += value;
	}

	/**
	 * Computes the costs of the given nodes and linked nodes of a nodepool.
	 * 
	 * @param nodePool
	 *            The nodepool to compare
	 * @param edgePool
	 *            The edgepool
	 * @return edgeCosts
	 */
	public double computeEdgeCosts(NodePool nodePool, EdgePool edgePool) {
		double value = 0;
		Iterator iterator = nodePool.getValueIterator();
		while (iterator.hasNext()) {
			Node compareNode = (Node) iterator.next();
			if (!compareNode.equals(this)) {
				// Check if there is an edge of this node and compareNode
				if (edgePool.contains(compareNode, this)) {
					// Add the weigth of the edge
					value += edgePool.getEdge(compareNode, this).getWeigth();
				}
			}
		}
		return value;
	}

	/**
	 * Updates the gain values of this node. The gain value indicates difference
	 * between external edge costs and internal edge costs.
	 * 
	 * @param externalPool
	 *            External nodePool
	 * @param internalPool
	 *            Internal nodePool
	 * @param edgePool
	 *            EdgePool of the graph
	 */
	public void updateGainValue(NodePool externalPool, NodePool internalPool,
			EdgePool edgePool) {
		// The gain value is the difference between external costs and internal
		// costs
		gainValue = (float) computeEdgeCosts(externalPool, edgePool)
				- (float) computeEdgeCosts(internalPool, edgePool);
	}

	/**
	 * @return Returns the weigth.
	 */
	public double getWeigth() {
		return weigth;
	}

	/**
	 * @param weigth
	 *            The weigth to set.
	 */
	public void setWeigth(float weigth) {
		this.weigth = weigth;
	}

	/**
	 * @return Returns the gainValue.
	 */
	public double getGainValue() {
		return gainValue;
	}

	/**
	 * @param gainValue
	 *            The gainValue to set.
	 */
	public void setGainValue(float gainValue) {
		this.gainValue = gainValue;
	}

	/**
	 * @return Returns the locked.
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @param locked
	 *            The locked to set.
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * @return Returns the partOfLeftSide.
	 */
	public boolean isPartOfLeftSide() {
		return partOfLeftSide;
	}

	/**
	 * @param partOfLeftSide
	 *            The partOfLeftSide to set.
	 */
	public void setPartOfLeftSide(boolean partOfLeftSide) {
		this.partOfLeftSide = partOfLeftSide;
	}

	/**
	 * @return the good
	 */
	public Good getGood() {
		return good;
	}

	/**
	 * @param good
	 *            the good to set
	 */
	public void setGood(Good good) {
		this.good = good;
	}

}
