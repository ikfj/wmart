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

/**
 * Encapsulates a nodepool and an edgepool by means of a bean
 * 
 * Created 15.11.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 */
public class NodeEdgeBean {

	private NodePool nodes = null;

	private EdgePool edges = null;

	/**
	 * Empty constructor New instances of nodepools and edgepools will be
	 * generated
	 */
	public NodeEdgeBean() {
		nodes = new NodePool();
		edges = new EdgePool();
	}

	/**
	 * @param edges
	 *            Edgepool to set
	 * @param nodes
	 *            Nodepool to set
	 */
	public NodeEdgeBean(EdgePool edges, NodePool nodes) {
		this.edges = edges;
		this.nodes = nodes;
	}

	/**
	 * @return Returns the edges.
	 */
	public EdgePool getEdges() {
		return edges;
	}

	/**
	 * @param edges
	 *            The edges to set.
	 */
	public void setEdges(EdgePool edges) {
		this.edges = edges;
	}

	/**
	 * @return Returns the nodes.
	 */
	public NodePool getNodes() {
		return nodes;
	}

	/**
	 * @param nodes
	 *            The nodes to set.
	 */
	public void setNodes(NodePool nodes) {
		this.nodes = nodes;
	}

}
