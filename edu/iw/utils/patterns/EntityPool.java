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
 * Created on 22.08.2004
 */
package edu.iw.utils.patterns;

import java.util.Iterator;
import java.util.TreeMap;

import edu.iw.mace.environment.MaceException;

/**
 * This pool stores any entity and encapsulates common parsing functionality
 * 
 * Created: 28.10.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public class EntityPool {

	TreeMap<String, Entity> treeMap = null;

	private Object[] values;

	/**
	 * Empty contructure of an EntityPool
	 */
	public EntityPool() {
		super();
		treeMap = new TreeMap<String, Entity>();
		updateObjectStructure();
	}

	/**
	 * Adds an entity to the pool
	 * 
	 * @param entity
	 *            Entity to add
	 */
	public void addEntity(Entity entity) throws MaceException {
		if (contains(entity))
			throw new MaceException("Cannot override " + entity.getId()
					+ ". It is already in the pool.");
		treeMap.put(entity.getId(), entity);
		updateObjectStructure();
	}

	/**
	 * Adds an item to the pool
	 * 
	 * @param id
	 *            ID of the item
	 * @param entity
	 *            Entity
	 */
	public void put(String id, Entity entity) throws MaceException {
		if (contains(id))
			throw new MaceException("Cannot override " + id
					+ ". It is already in the pool.");
		treeMap.put(id, entity);
		updateObjectStructure();
	}

	/**
	 * Get an entity from the pool by index
	 * 
	 * @param index entity index
	 * @return the entity
	 */
	public Entity getEntity(int index) {
		if (index > values.length - 1) {
			return null;
		}
		return (Entity) values[index];
	}

	/**
	 * Get an entity from the pool by identifier
	 * 
	 * @param id entity identifier
	 * @return the entity 
	 */
	public Entity getEntity(String id) {
		return treeMap.get(id);
	}

	/**
	 * Get an entity from the pool
	 * 
	 * @param entity
	 * @return an {@link Entity}
	 */
	public Entity getEntity(Entity entity) {
		return treeMap.get(entity.getId());
	}

	/**
	 * Removes an entity from the pool
	 * 
	 * @param entity Entity to remove
	 * @return <true> if entity was removed succesfully, <false> if not
	 */
	public boolean removeEntity(Entity entity) {
		if (treeMap.remove(entity.getId()) == null) {
			updateObjectStructure();
			return false;
		}
		updateObjectStructure();
		return true;
	}

	/**
	 * Removes an entity from the pool
	 * 
	 * @param id
	 *            Id to remove
	 * @return <true> if entity was removed succesfully, <false> if not
	 */
	public boolean removeEntity(String id) {
		if (treeMap.remove(id) == null) {
			updateObjectStructure();
			return false;
		}
		updateObjectStructure();
		return true;
	}

	/**
	 * Adds an existing EntityPool to the current entity Existing entities will
	 * not be replace
	 * 
	 * @param entityPool
	 *            The entitypool to add
	 */
	public void addEntityPool(EntityPool entityPool) throws MaceException {
		int size = entityPool.size();
		for (int a = 0; a < size; a++) {
			Entity entity = entityPool.getEntity(a);
			addEntity(entity);
		}
		updateObjectStructure();
	}

	/**
	 * Returns the size of the pool
	 * 
	 * @return Size
	 */
	public int size() {
		return treeMap.size();
	}

	/**
	 * Returns the index of a given entity
	 * 
	 * @param search
	 *            Entity to search for
	 * @return Index of the entity
	 */
	public int getIndex(Entity search) {
		int size = size();
		for (int a = 0; a < size; a++) {
			Entity entity = getEntity(a);
			if (entity.equals(search)) {
				return a;
			}
		}
		return -1;
	}

	/**
	 * Returns the values as an iterator
	 * 
	 * @return All values as an interator
	 */
	public Iterator<Entity> getValueIterator() {
		return treeMap.values().iterator();
	}

	/**
	 * Checks if an entity is in the pool or not
	 * 
	 * @param entity
	 *            Entity to search for
	 * @return <true> if the entity is in the pool, <false> if not
	 */
	public boolean contains(Entity entity) {
		return treeMap.containsKey(entity.getId());
	}

	/**
	 * Checks if an entity for a given id is in the pool or not
	 * 
	 * @param id
	 *            Id of the entity to search for
	 * @return <true> if the entity is in the pool, <false> if not
	 */
	public boolean contains(String id) {
		return treeMap.containsKey(id);
	}

	/**
	 * Updates the object structure, i.e. updates the (static) arrays in which
	 * objects are stored. This is usefull in dynamic environment.s
	 */
	public void updateObjectStructure() {
		values = treeMap.values().toArray();
	}

}
