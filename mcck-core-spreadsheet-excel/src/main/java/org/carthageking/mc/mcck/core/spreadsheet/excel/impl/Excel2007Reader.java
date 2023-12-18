package org.carthageking.mc.mcck.core.spreadsheet.excel.impl;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.carthageking.mc.mcck.core.jse.McckIOUtil;
import org.carthageking.mc.mcck.core.spreadsheet.SpreadsheetConstants;
import org.carthageking.mc.mcck.core.spreadsheet.SpreadsheetReader;
import org.carthageking.mc.mcck.core.spreadsheet.WorkbookIOException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class Excel2007Reader implements SpreadsheetReader {

	private final SAXParserFactory saxParserFactory;

	public Excel2007Reader(SAXParserFactory saxParserFactory) {
		this.saxParserFactory = saxParserFactory;
	}

	@Override
	public void readSpreadsheet(InputStream is, ContentHandler handler) {
		try (OPCPackage pkg = OPCPackage.open(is)) {
			readWorkbook(pkg, handler);
		} catch (SAXException | IOException | OpenXML4JException | ParserConfigurationException e) {
			throw new WorkbookIOException(e);
		}
	}

	@Override
	public void readSpreadsheet(File theFile, ContentHandler handler) {
		try (OPCPackage pkg = OPCPackage.open(theFile, PackageAccess.READ)) {
			readWorkbook(pkg, handler);
		} catch (SAXException | IOException | OpenXML4JException | ParserConfigurationException e) {
			throw new WorkbookIOException(e);
		}
	}

	// https://poi.apache.org/components/spreadsheet/how-to.html#xssf_sax_api
	private void readWorkbook(OPCPackage pkg, ContentHandler handler) throws SAXException, IOException, OpenXML4JException, ParserConfigurationException {
		XSSFReader xssfReader = new XSSFReader(pkg);
		SharedStrings ssTbl = xssfReader.getSharedStringsTable();
		StylesTable stylesTbl = xssfReader.getStylesTable();
		try {
			handler.startDocument();
			handler.startElement(null, SpreadsheetConstants.TAG_WORKBOOK, SpreadsheetConstants.TAG_WORKBOOK, new AttributesImpl());

			SAXParser saxParser = saxParserFactory.newSAXParser();
			int counter = -1;

			for (SheetIterator iter = (SheetIterator) xssfReader.getSheetsData(); iter.hasNext();) {
				counter++;
				try (InputStream sheetIs = iter.next()) {
					String sheetName = iter.getSheetName();

					AttributesImpl atts = new AttributesImpl();
					atts.addAttribute(null, SpreadsheetConstants.ATTR_INDEX, SpreadsheetConstants.ATTR_INDEX, null, String.valueOf(counter));
					atts.addAttribute(null, SpreadsheetConstants.ATTR_NAME, SpreadsheetConstants.ATTR_NAME, null, sheetName);

					handler.startElement(null, SpreadsheetConstants.TAG_SHEET, SpreadsheetConstants.TAG_SHEET, atts);

					InputSource isrc = new InputSource(sheetIs);
					saxParser.parse(isrc, new InternalXssfWorksheetHandler(ssTbl, stylesTbl, new DataFormatter(), handler));

					handler.endElement(null, SpreadsheetConstants.TAG_SHEET, SpreadsheetConstants.TAG_SHEET);
				}
			}

			handler.endElement(null, SpreadsheetConstants.TAG_WORKBOOK, SpreadsheetConstants.TAG_WORKBOOK);
			handler.endDocument();
		} finally {
			if (ssTbl instanceof AutoCloseable) {
				McckIOUtil.closeFully((AutoCloseable) ssTbl);
			} else if (ssTbl instanceof Closeable) {
				McckIOUtil.closeFully((Closeable) ssTbl);
			}
		}
	}

	// see org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler
	private class InternalXssfWorksheetHandler extends DefaultHandler {
		private final SharedStrings ssTbl;
		private final StylesTable stylesTbl;
		private final DataFormatter dataFormatter;
		private final ContentHandler handler;

		private StringBuilder formulaBuf = new StringBuilder();
		private StringBuilder valueBuf = new StringBuilder();
		private CellType cellType;
		private String styleIdxStr;
		private String cellTypeStr;
		private String cellRowRef;
		private boolean inFormula;

		public InternalXssfWorksheetHandler(SharedStrings ssTbl, StylesTable stylesTbl, DataFormatter dataFormatter, ContentHandler handler) {
			this.ssTbl = ssTbl;
			this.stylesTbl = stylesTbl;
			this.dataFormatter = dataFormatter;
			this.handler = handler;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			inFormula = false;
			formulaBuf.setLength(0);
			valueBuf.setLength(0);
			if ("c".equals(qName)) {
				// a cell
				cellRowRef = StringUtils.trimToEmpty(attributes.getValue("r"));
				styleIdxStr = StringUtils.trimToEmpty(attributes.getValue("s"));

				String type = StringUtils.trimToEmpty(attributes.getValue("t"));
				if (!StringUtils.isBlank(type)) {
					// string cell
					cellType = CellType.STRING;
					cellTypeStr = type;
				}
			} else if ("f".equals(qName)) {
				formulaBuf.setLength(0);
				inFormula = true;
				cellType = CellType.FORMULA;
			} else if ("v".equals(qName)) {
				valueBuf.setLength(0);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (inFormula) {
				formulaBuf.append(new String(ch, start, length));
			} else {
				valueBuf.append(new String(ch, start, length));
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if ("c".equals(qName) && !valueBuf.isEmpty()) {
				if (CellType.STRING.equals(cellType)) {
					RichTextString rts = ssTbl.getItemAt(Integer.valueOf(valueBuf.toString()));
					CellReference cr = new CellReference(cellRowRef);
					int row = cr.getRow();
					short col = cr.getCol();
					String origVal = rts.getString();

					AttributesImpl atts = new AttributesImpl();
					atts.addAttribute(null, SpreadsheetConstants.ATTR_DATATYPE, SpreadsheetConstants.ATTR_DATATYPE, null, SpreadsheetConstants.ATTR_DATATYPE_STRING);
					atts.addAttribute(null, SpreadsheetConstants.ATTR_ROW, SpreadsheetConstants.ATTR_ROW, null, String.valueOf(row));
					atts.addAttribute(null, SpreadsheetConstants.ATTR_COL, SpreadsheetConstants.ATTR_COL, null, String.valueOf(col));
					atts.addAttribute(null, SpreadsheetConstants.ATTR_CELLREF, SpreadsheetConstants.ATTR_CELLREF, null, cellRowRef);
					atts.addAttribute(null, SpreadsheetConstants.ATR_RAWVALUE, SpreadsheetConstants.ATR_RAWVALUE, null, String.valueOf(origVal));

					handler.startElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL, atts);
					handler.endElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL);
				} else if (CellType.FORMULA.equals(cellType)) {
					CellReference cr = new CellReference(cellRowRef);
					int row = cr.getRow();
					short col = cr.getCol();

					AttributesImpl atts = new AttributesImpl();
					atts.addAttribute(null, SpreadsheetConstants.ATTR_DATATYPE, SpreadsheetConstants.ATTR_DATATYPE, null, SpreadsheetConstants.ATTR_DATATYPE_FORMULA);
					atts.addAttribute(null, SpreadsheetConstants.ATTR_ROW, SpreadsheetConstants.ATTR_ROW, null, String.valueOf(row));
					atts.addAttribute(null, SpreadsheetConstants.ATTR_COL, SpreadsheetConstants.ATTR_COL, null, String.valueOf(col));
					atts.addAttribute(null, SpreadsheetConstants.ATTR_CELLREF, SpreadsheetConstants.ATTR_CELLREF, null, cellRowRef);

					if ("str".equals(cellTypeStr)) {
						atts.addAttribute(null, SpreadsheetConstants.ATTR_FORMULATYPE, SpreadsheetConstants.ATTR_FORMULATYPE, null, CellType.STRING.toString());
					} else {
						atts.addAttribute(null, SpreadsheetConstants.ATTR_FORMULATYPE, SpreadsheetConstants.ATTR_FORMULATYPE, null, CellType.NUMERIC.toString());
					}

					atts.addAttribute(null, SpreadsheetConstants.ATTR_FINALVALUE, SpreadsheetConstants.ATTR_FINALVALUE, null, valueBuf.toString());

					handler.startElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL, atts);
					handler.endElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL);
				} else {
					// a number cell
					CellReference cr = new CellReference(cellRowRef);
					int row = cr.getRow();
					short col = cr.getCol();
					String finalValue = valueBuf.toString();

					if (!StringUtils.isBlank(styleIdxStr)) {
						int styleIdx = Integer.valueOf(styleIdxStr);
						XSSFCellStyle cellStyle = stylesTbl.getStyleAt(styleIdx);
						if (null != cellStyle) {
							finalValue = dataFormatter.formatRawCellContents(Double.parseDouble(finalValue), cellStyle.getDataFormat(), cellStyle.getDataFormatString());
						}
					}

					AttributesImpl atts = new AttributesImpl();
					atts.addAttribute(null, SpreadsheetConstants.ATTR_DATATYPE, SpreadsheetConstants.ATTR_DATATYPE, null, SpreadsheetConstants.ATTR_DATATYPE_NUMBER);
					atts.addAttribute(null, SpreadsheetConstants.ATTR_ROW, SpreadsheetConstants.ATTR_ROW, null, String.valueOf(row));
					atts.addAttribute(null, SpreadsheetConstants.ATTR_COL, SpreadsheetConstants.ATTR_COL, null, String.valueOf(col));
					atts.addAttribute(null, SpreadsheetConstants.ATTR_CELLREF, SpreadsheetConstants.ATTR_CELLREF, null, cellRowRef);
					atts.addAttribute(null, SpreadsheetConstants.ATTR_FINALVALUE, SpreadsheetConstants.ATTR_FINALVALUE, null, finalValue);

					handler.startElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL, atts);
					handler.endElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL);
				}
				cellRowRef = null;
				cellTypeStr = null;
				cellType = null;
			} else if ("f".equals(qName)) {

			} else if ("v".equals(qName)) {

			}
		}
	}
}
