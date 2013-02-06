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
/*
 * Created on 20.08.2004
 */
package edu.iw.mace.environment;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Pool for managing buyer agents
 * 
 * Created: 25.09.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class BuyerAgentPool extends AbstractAgentPool {

	private static final long serialVersionUID = -2849946600902489058L;

	/**
	 * Empty constructor
	 */
	public BuyerAgentPool() {
		super();
	}

	/**
	 * Creates a new Agent instance and stores in in the pool
	 * 
	 * @return New agent instance
	 */
	public Agent createNewAgent()  throws MaceException{
		Agent agent = new Agent("n" + maxAgentId, Agent.AGENT_BUYER);
		addAgent(agent);
		maxAgentId++;
		return agent;
	}

	/**
	 * Creates a new Agent instance and stores in in the pool (Force Name
	 * Tagging (Id must be unique!))
	 * 
	 * @param id
	 *            Id of the new agent
	 * @return New agent instance
	 */
	public Agent createNewAgent(String id) throws AgentException, MaceException {
		// Check, if an agent with this Id is already in the pool
		if (getAgent(id) != null) {
			throw new AgentException("Buyer agent with ID=" + id
					+ " is already in the pool. Could not add it.");
		}
		Agent agent = new Agent(id, Agent.AGENT_BUYER);
		addAgent(agent);
		return agent;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.XMLExporter#toXML(org.w3c.dom.Document)
	 */
	@Override
	public Element toXML(Document document) {
		Iterator iterator = getValueIterator();
		Element element = document.createElement("buyer");
		while (iterator.hasNext()) {
			Agent agent = (Agent) iterator.next();
			element.appendChild(agent.toXML(document));
		}
		return element;
	}

}
