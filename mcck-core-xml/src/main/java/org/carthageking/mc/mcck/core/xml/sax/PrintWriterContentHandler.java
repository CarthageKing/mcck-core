package org.carthageking.mc.mcck.core.xml.sax;

import java.io.PrintWriter;

import org.apache.commons.text.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PrintWriterContentHandler extends DefaultHandler {

	protected final PrintWriter pwriter;
	protected final boolean includeCharacters;
	protected final boolean prettyPrint;

	protected int depth = 0;
	protected boolean alreadyPopped;

	public PrintWriterContentHandler(PrintWriter pw) {
		this(pw, true, false);
	}

	public PrintWriterContentHandler(PrintWriter pw, boolean includeCharacters) {
		this(pw, includeCharacters, false);
	}

	public PrintWriterContentHandler(PrintWriter pw, boolean includeCharacters, boolean prettyPrint) {
		this.pwriter = pw;
		this.includeCharacters = includeCharacters;
		this.prettyPrint = prettyPrint;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (depth > 0 && prettyPrint) {
			pwriter.println();
		}
		doIndent();
		depth++;
		alreadyPopped = false;

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
		if (alreadyPopped) {
			if (prettyPrint) {
				pwriter.println();
			}
			depth--;
			doIndent();
		} else {
			depth--;
		}
		alreadyPopped = true;

		pwriter.print("</");
		pwriter.print(qName);
		pwriter.print(">");
	}

	private void doIndent() {
		if (prettyPrint) {
			for (int i = 0; i < depth; i++) {
				pwriter.print("\t");
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (includeCharacters) {
			String str = new String(ch, start, length);
			pwriter.print(str);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		pwriter.flush();
	}
}
