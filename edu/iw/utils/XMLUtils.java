/*
 * Created on 20.08.2004
 */
package edu.iw.utils;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * A class that provides some XML utils
 * 
 * @author Bjoern Schnizler, University of Karlsruhe (TH)
 */
public class XMLUtils {

	private static Logger log = Logger.getLogger(XMLUtils.class);

	/**
	 * Creates a document builder instance
	 * 
	 * @return Document builder instance
	 * @throws ParserConfigurationException
	 */
	public static DocumentBuilder createDocumentBuilder()
			throws ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		return builder;
	}

	/**
	 * Transforms an XML document into another one according to a given XSLT
	 * 
	 * @param xmlInput
	 *            XML input document
	 * @param xslt
	 *            An URI to the XSLT sheet
	 * @param result
	 *            Output XML document
	 * @throws TransformerException
	 */
	public static void transformXML(Document xmlInput, URL xslt, Result result)
			throws TransformerException {

		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslt
				.toString()));

		Source source = new DOMSource(xmlInput);

		transformer.transform(source, result);

	}

	/**
	 * Transforms an XML document into another one according to a given XSLT
	 * 
	 * @param xmlInput
	 *            An URI to the XML input document
	 * @param xslt
	 *            An URI to the XSLT sheet
	 * @param result
	 *            Output XML document
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public static void transformXML(URL xmlInput, URL xslt, Result result)
			throws TransformerException, ParserConfigurationException,
			SAXException, IOException {

		Document environmentInput = XMLUtils.createDocumentBuilder().parse(
				xmlInput.getFile());

		transformXML(environmentInput, xslt, result);
	}

	/**
	 * Returns the attribute value of a given attribute
	 * 
	 * @param node
	 *            The node including the attribute
	 * @param attribute
	 *            The attribute name
	 * @return The attribute value
	 */
	public static String getAttributeValue(Node node, String attribute) {
		NamedNodeMap attributeMap = node.getAttributes();
		for (int i = 0; i < attributeMap.getLength(); i++) {
			Node a = attributeMap.item(i);
			if (a.getNodeName().equals(attribute)) {
				return a.getNodeValue();
			}
		}
		log.warn("Could not find attribute value for " + attribute
				+ " NodeName: " + node.getNodeName());
		return "";
	}
}
