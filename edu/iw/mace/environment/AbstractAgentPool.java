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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iw.utils.XMLExporter;
import edu.iw.utils.patterns.EntityPool;

/**
 * Abstract pool for managing agents
 * 
 * Created: 22.08.2004
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public abstract class AbstractAgentPool extends EntityPool implements
		XMLExporter {

	protected int maxAgentId = 0;

	/**
	 * Constructor of the agent pool
	 */
	public AbstractAgentPool() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.iw.utils.XMLExporter#toXML(org.w3c.dom.Document)
	 */
	public abstract Element toXML(Document document);

	/**
	 * @param agent
	 *            Agent to add
	 */
	public void addAgent(Agent agent) throws MaceException{
		addEntity(agent);
	}

	/**
	 * @param index
	 *            value of the agent
	 * @return Agent instance
	 */
	public Agent getAgent(int index) {
		return (Agent) getEntity(index);
	}

	/**
	 * @param id
	 *            of the agent
	 * @return Agent instance
	 */
	public Agent getAgent(String id) {
		return (Agent) getEntity(id);
	}
}
