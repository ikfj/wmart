/*
 * Created on 14.01.2005
 */
package edu.iw.utils;

import org.w3c.dom.Element;

/**
 * An interface for exporting data into an XML document
 * 
 * @author Bjoern Schnizler, University of Karlsruhe (TH)
 */
public interface XMLExporter {

	/**
	 * @param document
	 *            The XML document
	 * @return XML Element
	 */
	public Element toXML(org.w3c.dom.Document document);
}
