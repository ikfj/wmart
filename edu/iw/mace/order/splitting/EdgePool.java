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

import edu.iw.mace.environment.MaceException;
import edu.iw.utils.patterns.Entity;
import edu.iw.utils.patterns.EntityPool;

/**
 * Manages a set of edges
 * 
 * Created 05.11.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 */

public class EdgePool extends EntityPool {

	private static final long serialVersionUID = 5641518099268321872L;

	/**
	 * Adds an edge to the pool
	 * 
	 * @param edge
	 *            Edge to add
	 */
	public void addEdge(Edge edge) throws MaceException {
		addEntity(edge);
	}

	/**
	 * Checks if the pool contains an edge consisting of two given nodes
	 * 
	 * @param node1
	 *            First node
	 * @param node2
	 *            Second true
	 * @return <true> if the pool containts an edge containing the two given
	 *         nodes, <false> if not
	 */
	public boolean contains(Node node1, Node node2) {
		return this.contains(Edge.generateKey(node1, node2));
	}

	/**
	 * Returns an edge that contains the two given nodes
	 * 
	 * @param node1
	 *            First node
	 * @param node2
	 *            Second node
	 * @return An edge instance if one is found, <null> if no edge is found
	 */
	public Edge getEdge(Node node1, Node node2) {
		return (Edge) getEntity(Edge.generateKey(node1, node2));
	}

	/**
	 * Returns and edge for a given index value in the pool
	 * 
	 * @param index
	 * @return An edge instance if one is found, <null> if no edge is found
	 */
	public Edge getEdge(int index) {
		return (Edge) getEntity(index);
	}

	/**
	 * Returns and edge for a given id value in the pool
	 * 
	 * @param id
	 * @return An edge instance if one is found, <null> if no edge is found
	 */
	public Edge getEdge(String id) {
		return (Edge) getEntity(id);
	}

	/**
	 * Returns an edgepool containing all edges with one node in pool "left" and
	 * another node in pool "right"
	 * 
	 * @param left
	 *            NodePool with "left" Nodes
	 * @param right
	 *            NodePool with "right" Nodes
	 * @return EdgePool containing all overlapping edges
	 */
	public EdgePool getOverlappingEdges(NodePool left, NodePool right)
			throws MaceException {

		EdgePool result = new EdgePool();
		for (Iterator<Entity> edgesIterator = getValueIterator(); edgesIterator
				.hasNext();) {
			Edge edge = (Edge) edgesIterator.next();
			if (left.contains(edge.getNode1()) == true
					&& right.contains(edge.getNode2()) == true) {
				result.addEdge(edge);
			} else if (left.contains(edge.getNode2()) == true
					&& right.contains(edge.getNode1()) == true) {
				result.addEdge(edge);
			}
		}
		return result;
	}

}
