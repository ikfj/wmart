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

/**
 * An interface for an observer
 * 
 * Created: 28.10.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.1
 */
public interface Observer {

	/**
	 * Tell the observer that data has changed
	 * 
	 * @param subject
	 */
	void update(ObserverSubject subject);

}