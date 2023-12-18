package org.carthageking.mc.mcck.core.spreadsheet;

import java.io.File;
import java.io.InputStream;

import org.xml.sax.ContentHandler;

public interface SpreadsheetReader {

	void readSpreadsheet(InputStream is, ContentHandler handler);

	void readSpreadsheet(File theFile, ContentHandler handler);
}
