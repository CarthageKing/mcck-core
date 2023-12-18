package org.carthageking.mc.mcck.core.xml.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultHandlerContentHandlerAdapter extends DefaultHandler {

	private final ContentHandler handler;

	public DefaultHandlerContentHandlerAdapter(ContentHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		handler.setDocumentLocator(locator);
	}

	@Override
	public void startDocument() throws SAXException {
		handler.startDocument();
	}

	@Override
	public void declaration(String version, String encoding, String standalone) throws SAXException {
		handler.declaration(version, encoding, standalone);
	}

	@Override
	public void endDocument() throws SAXException {
		handler.endDocument();
	}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		handler.startPrefixMapping(prefix, uri);
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		handler.endPrefixMapping(prefix);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		handler.startElement(uri, localName, qName, atts);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		handler.endElement(uri, localName, qName);
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		handler.characters(ch, start, length);
	}

	@Override
	public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
		handler.ignorableWhitespace(ch, start, length);
	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		handler.processingInstruction(target, data);
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		handler.skippedEntity(name);
	}
}
