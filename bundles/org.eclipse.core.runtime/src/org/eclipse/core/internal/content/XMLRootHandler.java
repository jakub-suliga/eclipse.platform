/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.core.internal.content;

import java.io.IOException;
import javax.xml.parsers.*;
import org.eclipse.core.internal.runtime.InternalPlatform;
import org.osgi.framework.ServiceReference;
import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A content describer for detecting the name of the top-level element of the
 * DTD system identifier in an XML file. This supports two parameters:
 * <code>DTD_TO_FIND</code> and <code>ELEMENT_TO_FIND</code>. This is done
 * using the <code>IExecutableExtension</code> mechanism. If the
 * <code>":-"</code> method is used, then the value is treated as the
 * <code>ELEMENT_TO_FIND</code>.
 * 
 * @since 3.0
 */
public final class XMLRootHandler extends DefaultHandler implements LexicalHandler {
	/**
	 * An exception indicating that the parsing should stop. This is usually
	 * triggered when the top-level element has been found.
	 * 
	 * @since 3.0
	 */
	private class StopParsingException extends SAXException {
		/**
		 * Constructs an instance of <code>StopParsingException</code> with a
		 * <code>null</code> detail message.
		 */
		public StopParsingException() {
			super((String) null);
		}
	}

	/**
	 * Should we check the root element?
	 */
	private boolean checkRoot;
	/**
	 * The system identifier for the DTD that was found while parsing the XML.
	 * This member variable is <code>null</code> unless the file has been
	 * parsed successful to the point of finding the DTD's system identifier.
	 */
	private String dtdFound = null;
	/**
	 * This is the name of the top-level element found in the XML file. This
	 * member variable is <code>null</code> unless the file has been parsed
	 * successful to the point of finding the top-level element.
	 */
	private String elementFound = null;
	private SAXParserFactory factory;
	private boolean factoryFailed = false;

	public XMLRootHandler(boolean checkRoot) {
		this.checkRoot = checkRoot;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
	 */
	public final void comment(final char[] ch, final int start, final int length) {
		// Not interested.
	}

	/**
	 * Creates a new SAX parser for use within this instance.
	 * 
	 * @return The newly created parser.
	 * 
	 * @throws ParserConfigurationException
	 *             If a parser of the given configuration cannot be created.
	 * @throws SAXException
	 *             If something in general goes wrong when creating the parser.
	 * @throws SAXNotRecognizedException
	 *             If the <code>XMLReader</code> does not recognize the
	 *             lexical handler configuration option.
	 * @throws SAXNotSupportedException
	 *             If the <code>XMLReader</code> does not support the lexical
	 *             handler configuration option.
	 */
	private final SAXParser createParser(SAXParserFactory parserFactory) throws ParserConfigurationException, SAXException, SAXNotRecognizedException, SAXNotSupportedException {
		// Initialize the parser.
		final SAXParser parser = parserFactory.newSAXParser();
		final XMLReader reader = parser.getXMLReader();
		reader.setProperty("http://xml.org/sax/properties/lexical-handler", this); //$NON-NLS-1$
		return parser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#endCDATA()
	 */
	public final void endCDATA() {
		// Not interested.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#endDTD()
	 */
	public final void endDTD() {
		// Not interested.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
	 */
	public final void endEntity(final String name) {
		// Not interested.
	}

	public String getDTD() {
		return dtdFound;
	}

	private SAXParserFactory getFactory() {
		synchronized (this) {
			if (factoryFailed)
				return null;
			if (factory != null)
				return factory;
			ServiceReference parserReference = InternalPlatform.getDefault().getBundleContext().getServiceReference("javax.xml.parsers.SAXParserFactory"); //$NON-NLS-1$
			if (parserReference == null)
				return null;
			factory = (SAXParserFactory) InternalPlatform.getDefault().getBundleContext().getService(parserReference);
			if (factory == null)
				return null;
			factory.setNamespaceAware(true);
		}
		return factory;
	}

	public String getRootName() {
		return elementFound;
	}

	public boolean parseContents(InputSource contents) throws IOException, ParserConfigurationException {
		// Parse the file into we have what we need (or an error occurs).
		try {
			factory = getFactory();
			if (factory == null)
				return false;
			final SAXParser parser = createParser(factory);
			// to support external entities specified as relative URIs (see bug 63298) 
			contents.setSystemId("/"); //$NON-NLS-1$
			parser.parse(contents, this);
		} catch (final StopParsingException e) {
			// Abort the parsing normally.  Fall through...
		} catch (final SAXException e) {
			// we may be handed any kind of contents... it is normal we fail to parse
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#startCDATA()
	 */
	public final void startCDATA() {
		// Not interested.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public final void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
		dtdFound = systemId;
		// If we don't care about the top-level element, we can stop here.
		if (!checkRoot)
			throw new StopParsingException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public final void startElement(final String uri, final String elementName, final String qualifiedName, final Attributes attributes) throws SAXException {
		elementFound = elementName;
		throw new StopParsingException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
	 */
	public final void startEntity(final String name) {
		// Not interested.
	}
}