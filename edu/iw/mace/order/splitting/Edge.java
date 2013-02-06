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

import edu.iw.utils.patterns.Entity;

/**
 * This class represents an undirected Edge. An edge contains two nodes and has
 * a weight.
 * 
 * Created: 04.11.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class Edge extends Entity implements Cloneable {

	private Node node1;

	private Node node2;

	private float weigth;

	/**
	 * Constructs a new edge
	 * 
	 * @param node1
	 *            First node
	 * @param node2
	 *            Second node
	 * @param weigth
	 *            Initial weight
	 */
	public Edge(Node node1, Node node2, double weigth) {
		super();
		this.node1 = node1;
		this.node2 = node2;
		this.weigth = (float) weigth;
		setId(Edge.generateKey(node1, node2));
	}

	/**
	 * Generates a key containing node1 and node2
	 * 
	 * @param node1
	 *            Node1
	 * @param node2
	 *            Node2
	 * @return A unique key of Node1 and Node2
	 */
	public static String generateKey(Node node1, Node node2)
			throws NullPointerException {
		// Sort the goods
		if (node1 == null)
			throw new NullPointerException("Node1 is null");
		if (node2 == null)
			throw new NullPointerException("Node2 is null");
		if (node1.getId().compareTo(node2.getId()) < 0) {
			Node node3 = node1;
			node1 = node2;
			node2 = node3;
		}
		// Then return both ids as a new Id
		return node1.getId() + node2.getId();
	}

	/**
	 * Adds a value to the current weight of this edge
	 * 
	 * @param value
	 *            Value to add
	 */
	public void addWeight(double value) {
		this.weigth += value;
	}

	/**
	 * @return Returns the node1.
	 */
	public Node getNode1() {
		return node1;
	}

	/**
	 * @return Returns the node2.
	 */
	public Node getNode2() {
		return node2;
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

}
