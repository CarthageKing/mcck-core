package org.carthageking.mc.mcck.core.xml.sax;

import java.io.PrintWriter;

import org.apache.commons.text.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PrintWriterContentHandler extends DefaultHandler {

	protected final PrintWriter pwriter;
	protected final boolean includeCharacters;

	public PrintWriterContentHandler(PrintWriter pw) {
		this(pw, true);
	}

	public PrintWriterContentHandler(PrintWriter pw, boolean includeCharacters) {
		this.pwriter = pw;
		this.includeCharacters = includeCharacters;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		pwriter.print("<");
		pwriter.print(qName);
		if (atts.getLength() > 0) {
			pwriter.print(" ");
		}
		for (int i = 0; i < atts.getLength(); i++) {
			if (i > 0) {
				pwriter.print(" ");
			}
			pwriter.print(atts.getQName(i));
			pwriter.print("=\"");
			pwriter.print(StringEscapeUtils.escapeXml11(atts.getValue(i)));
			pwriter.print("\"");
		}
		pwriter.print(">");
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		pwriter.print("</");
		pwriter.print(qName);
		pwriter.print(">");
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (includeCharacters) {
			pwriter.print(new String(ch, start, length));
		}
	}

	@Override
	public void endDocument() throws SAXException {
		pwriter.flush();
	}
}
