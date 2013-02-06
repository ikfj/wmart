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

package edu.iw.utils.patterns;

import edu.iw.mace.environment.MaceException;
import edu.iw.utils.RandomGUID;

/**
 * An abstract entity encapsulates everything that has an <id>
 * 
 * Created: 31.10.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class Entity {

	private String id = "";

	/**
	 * Constructor of an abstract entity
	 * 
	 * @param id
	 *            The id of an entity
	 */
	public Entity(String id) {
		this.id = id;
	}

	/**
	 * Empty constructor
	 */
	public Entity() {
		this("");
	}

	/**
	 * Generates a new id for this entity
	 */
	public void generateNewId() throws MaceException {
		RandomGUID guid = new RandomGUID(false);
		setId(guid.toString());
	}

	/**
	 * @param entity
	 *            Entity to compare
	 * @return <true> if this entity and the parameter entity are equal, <false>
	 *         if not
	 */
	public boolean equals(Entity entity) {
		return this.id.equals(entity.getId());
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

}
