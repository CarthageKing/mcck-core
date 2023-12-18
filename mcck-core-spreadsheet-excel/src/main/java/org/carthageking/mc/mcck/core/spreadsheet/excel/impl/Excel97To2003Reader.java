package org.carthageking.mc.mcck.core.spreadsheet.excel.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.ExtSSTRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.GenericRecordUtil.AnnotatedFlag;
import org.carthageking.mc.mcck.core.jse.McckUtil;
import org.carthageking.mc.mcck.core.spreadsheet.SpreadsheetConstants;
import org.carthageking.mc.mcck.core.spreadsheet.SpreadsheetReader;
import org.carthageking.mc.mcck.core.spreadsheet.WorkbookIOException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Excel97To2003Reader implements SpreadsheetReader {

	public Excel97To2003Reader() {
		// noop
	}

	@Override
	public void readSpreadsheet(InputStream is, ContentHandler handler) {
		try (POIFSFileSystem poifs = new POIFSFileSystem(is)) {
			readWorkbook(poifs, handler);
		} catch (SAXException | IOException e) {
			throw new WorkbookIOException(e);
		}
	}

	@Override
	public void readSpreadsheet(File theFile, ContentHandler handler) {
		try (POIFSFileSystem poifs = new POIFSFileSystem(theFile, true)) {
			readWorkbook(poifs, handler);
		} catch (SAXException | IOException e) {
			throw new WorkbookIOException(e);
		}
	}

	private void readWorkbook(POIFSFileSystem poifs, ContentHandler handler) throws SAXException, IOException {
		HSSFEventFactory eventFactory = new HSSFEventFactory();
		HSSFRequest request = new HSSFRequest();
		InternalHssfListener internalListener = new InternalHssfListener(handler);
		FormatTrackingHSSFListener formatTrackingListener = new FormatTrackingHSSFListener(internalListener);
		internalListener.setFormatTrackingListener(formatTrackingListener);
		request.addListenerForAllRecords(formatTrackingListener);
		eventFactory.processWorkbookEvents(request, poifs);
		internalListener.finalizeEvents();
	}

	// https://poi.apache.org/components/spreadsheet/how-to.html#event_api
	private class InternalHssfListener implements HSSFListener {

		private final ContentHandler handler;
		private final Map<Short, HssfRecordProcessor> processors = new HashMap<>();

		private SSTRecord sstRecord;
		private ExtSSTRecord extSstRecord;
		private FormatTrackingHSSFListener formatTrackingListener;
		private List<BoundSheetRecord> boundSheetRecords = new ArrayList<>();
		private boolean boundSheetListFinalized;
		private int sheetIndexCounter = -1;
		private List<BOFRecord> bofStack = new ArrayList<>();

		private AttributesImpl lastAttributes;

		public InternalHssfListener(ContentHandler handler) {
			this.handler = handler;
			processors.put(HSSFRecordTypes.AREA.getSid(), this::processAREARecord);
			processors.put(HSSFRecordTypes.AREA_FORMAT.getSid(), this::processAREA_FORMATRecord);
			processors.put(HSSFRecordTypes.ARRAY.getSid(), this::processARRAYRecord);
			processors.put(HSSFRecordTypes.AUTO_FILTER_INFO.getSid(), this::processAUTO_FILTER_INFORecord);
			processors.put(HSSFRecordTypes.AXIS.getSid(), this::processAXISRecord);
			processors.put(HSSFRecordTypes.AXIS_LINE_FORMAT.getSid(), this::processAXIS_LINE_FORMATRecord);
			processors.put(HSSFRecordTypes.AXIS_OPTIONS.getSid(), this::processAXIS_OPTIONSRecord);
			processors.put(HSSFRecordTypes.AXIS_PARENT.getSid(), this::processAXIS_PARENTRecord);
			processors.put(HSSFRecordTypes.AXIS_USED.getSid(), this::processAXIS_USEDRecord);
			processors.put(HSSFRecordTypes.BACKUP.getSid(), this::processBACKUPRecord);
			processors.put(HSSFRecordTypes.BAR.getSid(), this::processBARRecord);
			processors.put(HSSFRecordTypes.BEGIN.getSid(), this::processBEGINRecord);
			processors.put(HSSFRecordTypes.BLANK.getSid(), this::processBLANKRecord);
			processors.put(HSSFRecordTypes.BOF.getSid(), this::processBOFRecord);
			processors.put(HSSFRecordTypes.BOOK_BOOL.getSid(), this::processBOOK_BOOLRecord);
			processors.put(HSSFRecordTypes.BOOL_ERR.getSid(), this::processBOOL_ERRRecord);
			processors.put(HSSFRecordTypes.BOTTOM_MARGIN.getSid(), this::processBOTTOM_MARGINRecord);
			processors.put(HSSFRecordTypes.BOUND_SHEET.getSid(), this::processBOUND_SHEETRecord);
			processors.put(HSSFRecordTypes.CALC_COUNT.getSid(), this::processCALC_COUNTRecord);
			processors.put(HSSFRecordTypes.CALC_MODE.getSid(), this::processCALC_MODERecord);
			processors.put(HSSFRecordTypes.CAT_LAB.getSid(), this::processCAT_LABRecord);
			processors.put(HSSFRecordTypes.CATEGORY_SERIES_AXIS.getSid(), this::processCATEGORY_SERIES_AXISRecord);
			processors.put(HSSFRecordTypes.CF_HEADER.getSid(), this::processCF_HEADERRecord);
			processors.put(HSSFRecordTypes.CF_HEADER_12.getSid(), this::processCF_HEADER_12Record);
			processors.put(HSSFRecordTypes.CF_RULE.getSid(), this::processCF_RULERecord);
			processors.put(HSSFRecordTypes.CF_RULE_12.getSid(), this::processCF_RULE_12Record);
			processors.put(HSSFRecordTypes.CHART.getSid(), this::processCHARTRecord);
			processors.put(HSSFRecordTypes.CHART_END_BLOCK.getSid(), this::processCHART_END_BLOCKRecord);
			processors.put(HSSFRecordTypes.CHART_END_OBJECT.getSid(), this::processCHART_END_OBJECTRecord);
			processors.put(HSSFRecordTypes.CHART_FORMAT.getSid(), this::processCHART_FORMATRecord);
			processors.put(HSSFRecordTypes.CHART_FRT_INFO.getSid(), this::processCHART_FRT_INFORecord);
			processors.put(HSSFRecordTypes.CHART_START_BLOCK.getSid(), this::processCHART_START_BLOCKRecord);
			processors.put(HSSFRecordTypes.CHART_START_OBJECT.getSid(), this::processCHART_START_OBJECTRecord);
			processors.put(HSSFRecordTypes.CHART_TITLE_FORMAT.getSid(), this::processCHART_TITLE_FORMATRecord);
			processors.put(HSSFRecordTypes.CODEPAGE.getSid(), this::processCODEPAGERecord);
			processors.put(HSSFRecordTypes.COLUMN_INFO.getSid(), this::processCOLUMN_INFORecord);
			processors.put(HSSFRecordTypes.CONTINUE.getSid(), this::processCONTINUERecord);
			processors.put(HSSFRecordTypes.COUNTRY.getSid(), this::processCOUNTRYRecord);
			processors.put(HSSFRecordTypes.CRN.getSid(), this::processCRNRecord);
			processors.put(HSSFRecordTypes.CRN_COUNT.getSid(), this::processCRN_COUNTRecord);
			processors.put(HSSFRecordTypes.DAT.getSid(), this::processDATRecord);
			processors.put(HSSFRecordTypes.DATA_FORMAT.getSid(), this::processDATA_FORMATRecord);
			processors.put(HSSFRecordTypes.DATA_ITEM.getSid(), this::processDATA_ITEMRecord);
			processors.put(HSSFRecordTypes.DATA_LABEL_EXTENSION.getSid(), this::processDATA_LABEL_EXTENSIONRecord);
			processors.put(HSSFRecordTypes.DATE_WINDOW_1904.getSid(), this::processDATE_WINDOW_1904Record);
			processors.put(HSSFRecordTypes.DB_CELL.getSid(), this::processDB_CELLRecord);
			processors.put(HSSFRecordTypes.DCON_REF.getSid(), this::processDCON_REFRecord);
			processors.put(HSSFRecordTypes.DEFAULT_COL_WIDTH.getSid(), this::processDEFAULT_COL_WIDTHRecord);
			processors.put(HSSFRecordTypes.DEFAULT_DATA_LABEL_TEXT_PROPERTIES.getSid(), this::processDEFAULT_DATA_LABEL_TEXT_PROPERTIESRecord);
			processors.put(HSSFRecordTypes.DEFAULT_ROW_HEIGHT.getSid(), this::processDEFAULT_ROW_HEIGHTRecord);
			processors.put(HSSFRecordTypes.DELTA.getSid(), this::processDELTARecord);
			processors.put(HSSFRecordTypes.DIMENSIONS.getSid(), this::processDIMENSIONSRecord);
			processors.put(HSSFRecordTypes.DRAWING.getSid(), this::processDRAWINGRecord);
			processors.put(HSSFRecordTypes.DRAWING_GROUP.getSid(), this::processDRAWING_GROUPRecord);
			processors.put(HSSFRecordTypes.DRAWING_SELECTION.getSid(), this::processDRAWING_SELECTIONRecord);
			processors.put(HSSFRecordTypes.DSF.getSid(), this::processDSFRecord);
			processors.put(HSSFRecordTypes.DV.getSid(), this::processDVRecord);
			processors.put(HSSFRecordTypes.DVAL.getSid(), this::processDVALRecord);
			processors.put(HSSFRecordTypes.END.getSid(), this::processENDRecord);
			processors.put(HSSFRecordTypes.EOF.getSid(), this::processEOFRecord);
			processors.put(HSSFRecordTypes.ESCHER_AGGREGATE.getSid(), this::processESCHER_AGGREGATERecord);
			processors.put(HSSFRecordTypes.EXT_SST.getSid(), this::processEXT_SSTRecord);
			processors.put(HSSFRecordTypes.EXTENDED_FORMAT.getSid(), this::processEXTENDED_FORMATRecord);
			processors.put(HSSFRecordTypes.EXTENDED_PIVOT_TABLE_VIEW_FIELDS.getSid(), this::processEXTENDED_PIVOT_TABLE_VIEW_FIELDSRecord);
			processors.put(HSSFRecordTypes.EXTERN_SHEET.getSid(), this::processEXTERN_SHEETRecord);
			processors.put(HSSFRecordTypes.EXTERNAL_NAME.getSid(), this::processEXTERNAL_NAMERecord);
			processors.put(HSSFRecordTypes.FEAT.getSid(), this::processFEATRecord);
			processors.put(HSSFRecordTypes.FEAT_HDR.getSid(), this::processFEAT_HDRRecord);
			processors.put(HSSFRecordTypes.FILE_PASS.getSid(), this::processFILE_PASSRecord);
			processors.put(HSSFRecordTypes.FILE_SHARING.getSid(), this::processFILE_SHARINGRecord);
			processors.put(HSSFRecordTypes.FN_GROUP_COUNT.getSid(), this::processFN_GROUP_COUNTRecord);
			processors.put(HSSFRecordTypes.FONT.getSid(), this::processFONTRecord);
			processors.put(HSSFRecordTypes.FONT_BASIS.getSid(), this::processFONT_BASISRecord);
			processors.put(HSSFRecordTypes.FONT_INDEX.getSid(), this::processFONT_INDEXRecord);
			processors.put(HSSFRecordTypes.FOOTER.getSid(), this::processFOOTERRecord);
			processors.put(HSSFRecordTypes.FORMAT.getSid(), this::processFORMATRecord);
			processors.put(HSSFRecordTypes.FORMULA.getSid(), this::processFORMULARecord);
			processors.put(HSSFRecordTypes.FRAME.getSid(), this::processFRAMERecord);
			processors.put(HSSFRecordTypes.GRIDSET.getSid(), this::processGRIDSETRecord);
			processors.put(HSSFRecordTypes.GUTS.getSid(), this::processGUTSRecord);
			processors.put(HSSFRecordTypes.H_CENTER.getSid(), this::processH_CENTERRecord);
			processors.put(HSSFRecordTypes.HEADER.getSid(), this::processHEADERRecord);
			processors.put(HSSFRecordTypes.HEADER_FOOTER.getSid(), this::processHEADER_FOOTERRecord);
			processors.put(HSSFRecordTypes.HIDE_OBJ.getSid(), this::processHIDE_OBJRecord);
			processors.put(HSSFRecordTypes.HORIZONTAL_PAGE_BREAK.getSid(), this::processHORIZONTAL_PAGE_BREAKRecord);
			processors.put(HSSFRecordTypes.HYPERLINK.getSid(), this::processHYPERLINKRecord);
			processors.put(HSSFRecordTypes.INDEX.getSid(), this::processINDEXRecord);
			processors.put(HSSFRecordTypes.INTERFACE_END.getSid(), this::processINTERFACE_ENDRecord);
			processors.put(HSSFRecordTypes.INTERFACE_HDR.getSid(), this::processINTERFACE_HDRRecord);
			processors.put(HSSFRecordTypes.ITERATION.getSid(), this::processITERATIONRecord);
			processors.put(HSSFRecordTypes.LABEL.getSid(), this::processLABELRecord);
			processors.put(HSSFRecordTypes.LABEL_SST.getSid(), this::processLABEL_SSTRecord);
			processors.put(HSSFRecordTypes.LEFT_MARGIN.getSid(), this::processLEFT_MARGINRecord);
			processors.put(HSSFRecordTypes.LEGEND.getSid(), this::processLEGENDRecord);
			processors.put(HSSFRecordTypes.LINE_FORMAT.getSid(), this::processLINE_FORMATRecord);
			processors.put(HSSFRecordTypes.LINKED_DATA.getSid(), this::processLINKED_DATARecord);
			processors.put(HSSFRecordTypes.MERGE_CELLS.getSid(), this::processMERGE_CELLSRecord);
			processors.put(HSSFRecordTypes.MMS.getSid(), this::processMMSRecord);
			processors.put(HSSFRecordTypes.MUL_BLANK.getSid(), this::processMUL_BLANKRecord);
			processors.put(HSSFRecordTypes.MUL_RK.getSid(), this::processMUL_RKRecord);
			processors.put(HSSFRecordTypes.NAME.getSid(), this::processNAMERecord);
			processors.put(HSSFRecordTypes.NAME_COMMENT.getSid(), this::processNAME_COMMENTRecord);
			processors.put(HSSFRecordTypes.NOTE.getSid(), this::processNOTERecord);
			processors.put(HSSFRecordTypes.NUMBER.getSid(), this::processNUMBERRecord);
			processors.put(HSSFRecordTypes.NUMBER_FORMAT_INDEX.getSid(), this::processNUMBER_FORMAT_INDEXRecord);
			processors.put(HSSFRecordTypes.OBJ.getSid(), this::processOBJRecord);
			processors.put(HSSFRecordTypes.OBJECT_LINK.getSid(), this::processOBJECT_LINKRecord);
			processors.put(HSSFRecordTypes.OBJECT_PROTECT.getSid(), this::processOBJECT_PROTECTRecord);
			processors.put(HSSFRecordTypes.PAGE_ITEM.getSid(), this::processPAGE_ITEMRecord);
			processors.put(HSSFRecordTypes.PALETTE.getSid(), this::processPALETTERecord);
			processors.put(HSSFRecordTypes.PANE.getSid(), this::processPANERecord);
			processors.put(HSSFRecordTypes.PASSWORD.getSid(), this::processPASSWORDRecord);
			processors.put(HSSFRecordTypes.PASSWORD_REV_4.getSid(), this::processPASSWORD_REV_4Record);
			processors.put(HSSFRecordTypes.PLOT_AREA.getSid(), this::processPLOT_AREARecord);
			processors.put(HSSFRecordTypes.PLOT_GROWTH.getSid(), this::processPLOT_GROWTHRecord);
			processors.put(HSSFRecordTypes.PRECISION.getSid(), this::processPRECISIONRecord);
			processors.put(HSSFRecordTypes.PRINT_GRIDLINES.getSid(), this::processPRINT_GRIDLINESRecord);
			processors.put(HSSFRecordTypes.PRINT_HEADERS.getSid(), this::processPRINT_HEADERSRecord);
			processors.put(HSSFRecordTypes.PRINT_SETUP.getSid(), this::processPRINT_SETUPRecord);
			processors.put(HSSFRecordTypes.PROTECT.getSid(), this::processPROTECTRecord);
			processors.put(HSSFRecordTypes.PROTECTION_REV_4.getSid(), this::processPROTECTION_REV_4Record);
			processors.put(HSSFRecordTypes.RECALC_ID.getSid(), this::processRECALC_IDRecord);
			processors.put(HSSFRecordTypes.REF_MODE.getSid(), this::processREF_MODERecord);
			processors.put(HSSFRecordTypes.REFRESH_ALL.getSid(), this::processREFRESH_ALLRecord);
			processors.put(HSSFRecordTypes.RIGHT_MARGIN.getSid(), this::processRIGHT_MARGINRecord);
			processors.put(HSSFRecordTypes.RK.getSid(), this::processRKRecord);
			processors.put(HSSFRecordTypes.ROW.getSid(), this::processROWRecord);
			processors.put(HSSFRecordTypes.SAVE_RECALC.getSid(), this::processSAVE_RECALCRecord);
			processors.put(HSSFRecordTypes.SCENARIO_PROTECT.getSid(), this::processSCENARIO_PROTECTRecord);
			processors.put(HSSFRecordTypes.SCL.getSid(), this::processSCLRecord);
			processors.put(HSSFRecordTypes.SELECTION.getSid(), this::processSELECTIONRecord);
			processors.put(HSSFRecordTypes.SERIES.getSid(), this::processSERIESRecord);
			processors.put(HSSFRecordTypes.SERIES_CHART_GROUP_INDEX.getSid(), this::processSERIES_CHART_GROUP_INDEXRecord);
			processors.put(HSSFRecordTypes.SERIES_INDEX.getSid(), this::processSERIES_INDEXRecord);
			processors.put(HSSFRecordTypes.SERIES_LABELS.getSid(), this::processSERIES_LABELSRecord);
			processors.put(HSSFRecordTypes.SERIES_LIST.getSid(), this::processSERIES_LISTRecord);
			processors.put(HSSFRecordTypes.SERIES_TEXT.getSid(), this::processSERIES_TEXTRecord);
			processors.put(HSSFRecordTypes.SHARED_FORMULA.getSid(), this::processSHARED_FORMULARecord);
			processors.put(HSSFRecordTypes.SHEET_PROPERTIES.getSid(), this::processSHEET_PROPERTIESRecord);
			processors.put(HSSFRecordTypes.SST.getSid(), this::processSSTRecord);
			processors.put(HSSFRecordTypes.STREAM_ID.getSid(), this::processSTREAM_IDRecord);
			processors.put(HSSFRecordTypes.STRING.getSid(), this::processSTRINGRecord);
			processors.put(HSSFRecordTypes.STYLE.getSid(), this::processSTYLERecord);
			processors.put(HSSFRecordTypes.SUP_BOOK.getSid(), this::processSUP_BOOKRecord);
			processors.put(HSSFRecordTypes.TAB_ID.getSid(), this::processTAB_IDRecord);
			processors.put(HSSFRecordTypes.TABLE.getSid(), this::processTABLERecord);
			processors.put(HSSFRecordTypes.TABLE_STYLES.getSid(), this::processTABLE_STYLESRecord);
			processors.put(HSSFRecordTypes.TEXT.getSid(), this::processTEXTRecord);
			processors.put(HSSFRecordTypes.TEXT_OBJECT.getSid(), this::processTEXT_OBJECTRecord);
			processors.put(HSSFRecordTypes.TICK.getSid(), this::processTICKRecord);
			processors.put(HSSFRecordTypes.TOP_MARGIN.getSid(), this::processTOP_MARGINRecord);
			processors.put(HSSFRecordTypes.UNCALCED.getSid(), this::processUNCALCEDRecord);
			processors.put(HSSFRecordTypes.UNITS.getSid(), this::processUNITSRecord);
			processors.put(HSSFRecordTypes.USE_SEL_FS.getSid(), this::processUSE_SEL_FSRecord);
			processors.put(HSSFRecordTypes.USER_SVIEW_BEGIN.getSid(), this::processUSER_SVIEW_BEGINRecord);
			processors.put(HSSFRecordTypes.USER_SVIEW_END.getSid(), this::processUSER_SVIEW_ENDRecord);
			processors.put(HSSFRecordTypes.V_CENTER.getSid(), this::processV_CENTERRecord);
			processors.put(HSSFRecordTypes.VALUE_RANGE.getSid(), this::processVALUE_RANGERecord);
			processors.put(HSSFRecordTypes.VERTICAL_PAGE_BREAK.getSid(), this::processVERTICAL_PAGE_BREAKRecord);
			processors.put(HSSFRecordTypes.VIEW_DEFINITION.getSid(), this::processVIEW_DEFINITIONRecord);
			processors.put(HSSFRecordTypes.VIEW_FIELDS.getSid(), this::processVIEW_FIELDSRecord);
			processors.put(HSSFRecordTypes.VIEW_SOURCE.getSid(), this::processVIEW_SOURCERecord);
			processors.put(HSSFRecordTypes.WINDOW_ONE.getSid(), this::processWINDOW_ONERecord);
			processors.put(HSSFRecordTypes.WINDOW_PROTECT.getSid(), this::processWINDOW_PROTECTRecord);
			processors.put(HSSFRecordTypes.WINDOW_TWO.getSid(), this::processWINDOW_TWORecord);
			processors.put(HSSFRecordTypes.WRITE_ACCESS.getSid(), this::processWRITE_ACCESSRecord);
			processors.put(HSSFRecordTypes.WRITE_PROTECT.getSid(), this::processWRITE_PROTECTRecord);
			processors.put(HSSFRecordTypes.WS_BOOL.getSid(), this::processWS_BOOLRecord);
		}

		public void setFormatTrackingListener(FormatTrackingHSSFListener formatTrackingListener) {
			this.formatTrackingListener = formatTrackingListener;
		}

		public void finalizeEvents() throws SAXException {
			handler.endElement(null, SpreadsheetConstants.TAG_WORKBOOK, SpreadsheetConstants.TAG_WORKBOOK);
			handler.endDocument();
		}

		@Override
		public void processRecord(Record theRecord) {
			HssfRecordProcessor processor = processors.get(theRecord.getSid());
			if (null == processor) {
				System.err.println("Unsupported record type " + theRecord.getSid());
				//throw new WorkbookIOException("Unsupported record type " + theRecord.getSid());
			} else {
				try {
					processor.process(theRecord);
				} catch (Exception e) {
					throw new WorkbookIOException(e);
				}
			}
		}

		private void processAREARecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processAREA_FORMATRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processARRAYRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processAUTO_FILTER_INFORecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processAXISRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processAXIS_LINE_FORMATRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processAXIS_OPTIONSRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processAXIS_PARENTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processAXIS_USEDRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processBACKUPRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processBARRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processBEGINRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processBLANKRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processBOFRecord(Record theRecord) throws Exception {
			BOFRecord bof = (BOFRecord) theRecord;
			if (BOFRecord.TYPE_WORKBOOK == bof.getType()) {
				handler.startDocument();
				handler.startElement(null, SpreadsheetConstants.TAG_WORKBOOK, SpreadsheetConstants.TAG_WORKBOOK, new AttributesImpl());
			} else if (BOFRecord.TYPE_WORKSHEET == bof.getType()) {
				finalizeBoundSheetList();
				sheetIndexCounter++;
				BoundSheetRecord bsr = boundSheetRecords.get(sheetIndexCounter);

				AttributesImpl atts = new AttributesImpl();
				atts.addAttribute(null, SpreadsheetConstants.ATTR_INDEX, SpreadsheetConstants.ATTR_INDEX, null, String.valueOf(sheetIndexCounter));
				atts.addAttribute(null, SpreadsheetConstants.ATTR_NAME, SpreadsheetConstants.ATTR_NAME, null, bsr.getSheetname());

				handler.startElement(null, SpreadsheetConstants.TAG_SHEET, SpreadsheetConstants.TAG_SHEET, atts);
			} else {
				genericStartEndElement(theRecord, true, false);
			}
			pushBof(bof);
		}

		private void finalizeBoundSheetList() {
			if (!boundSheetListFinalized) {
				boundSheetRecords = Collections.unmodifiableList(Arrays.asList(BoundSheetRecord.orderByBofPosition(boundSheetRecords)));
				boundSheetListFinalized = true;
			}
		}

		private void processBOOK_BOOLRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processBOOL_ERRRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processBOTTOM_MARGINRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processBOUND_SHEETRecord(Record theRecord) throws Exception {
			boundSheetRecords.add((BoundSheetRecord) theRecord);
		}

		private void processCALC_COUNTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCALC_MODERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCAT_LABRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCATEGORY_SERIES_AXISRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCF_HEADERRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCF_HEADER_12Record(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCF_RULERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCF_RULE_12Record(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCHARTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCHART_END_BLOCKRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCHART_END_OBJECTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCHART_FORMATRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCHART_FRT_INFORecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCHART_START_BLOCKRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCHART_START_OBJECTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCHART_TITLE_FORMATRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCODEPAGERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCOLUMN_INFORecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCONTINUERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCOUNTRYRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCRNRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processCRN_COUNTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDATRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDATA_FORMATRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDATA_ITEMRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDATA_LABEL_EXTENSIONRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDATE_WINDOW_1904Record(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDB_CELLRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDCON_REFRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDEFAULT_COL_WIDTHRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDEFAULT_DATA_LABEL_TEXT_PROPERTIESRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDEFAULT_ROW_HEIGHTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDELTARecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDIMENSIONSRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDRAWINGRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDRAWING_GROUPRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDRAWING_SELECTIONRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDSFRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDVRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processDVALRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processENDRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processEOFRecord(Record theRecord) throws Exception {
			BOFRecord bof = popBof();
			if (BOFRecord.TYPE_WORKBOOK == bof.getType()) {
				// do not output closing tag
			} else if (BOFRecord.TYPE_WORKSHEET == bof.getType()) {
				handler.endElement(null, SpreadsheetConstants.TAG_SHEET, SpreadsheetConstants.TAG_SHEET);
			} else {
				genericStartEndElement(bof, false, true);
			}
		}

		private void processESCHER_AGGREGATERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processEXT_SSTRecord(Record theRecord) throws Exception {
			extSstRecord = (ExtSSTRecord) theRecord;
		}

		private void processEXTENDED_FORMATRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processEXTENDED_PIVOT_TABLE_VIEW_FIELDSRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processEXTERN_SHEETRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processEXTERNAL_NAMERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processFEATRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processFEAT_HDRRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processFILE_PASSRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processFILE_SHARINGRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processFN_GROUP_COUNTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processFONTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processFONT_BASISRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processFONT_INDEXRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processFOOTERRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processFORMATRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processFORMULARecord(Record theRecord) throws Exception {
			if (!isWithinWorkbookOrSheet()) {
				return;
			}
			//genericStartEndElement(theRecord, true, true);
			FormulaRecord fRec = (FormulaRecord) theRecord;
			int row = fRec.getRow();
			short col = fRec.getColumn();

			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute(null, SpreadsheetConstants.ATTR_DATATYPE, SpreadsheetConstants.ATTR_DATATYPE, null, SpreadsheetConstants.ATTR_DATATYPE_FORMULA);
			atts.addAttribute(null, SpreadsheetConstants.ATTR_ROW, SpreadsheetConstants.ATTR_ROW, null, String.valueOf(row));
			atts.addAttribute(null, SpreadsheetConstants.ATTR_COL, SpreadsheetConstants.ATTR_COL, null, String.valueOf(col));
			atts.addAttribute(null, SpreadsheetConstants.ATTR_CELLREF, SpreadsheetConstants.ATTR_CELLREF, null, new CellReference(row, col).formatAsString(false));

			CellType cellType = fRec.getCachedResultTypeEnum();

			if (CellType.NUMERIC.equals(cellType)) {
				atts.addAttribute(null, SpreadsheetConstants.ATTR_FORMULATYPE, SpreadsheetConstants.ATTR_FORMULATYPE, null, CellType.NUMERIC.toString());
				atts.addAttribute(null, SpreadsheetConstants.ATTR_FINALVALUE, SpreadsheetConstants.ATTR_FINALVALUE, null, formatTrackingListener.formatNumberDateCell(fRec));

				handler.startElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL, atts);
				handler.endElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL);
			} else if (CellType.STRING.equals(cellType)) {
				// the next record after this should be a StringRecord. get the finalValue from there
				atts.addAttribute(null, SpreadsheetConstants.ATTR_FORMULATYPE, SpreadsheetConstants.ATTR_FORMULATYPE, null, CellType.STRING.toString());
				lastAttributes = atts;
			} else {
				genericStartEndElement(theRecord, true, true);
			}
		}

		private void processFRAMERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processGRIDSETRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processGUTSRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processH_CENTERRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processHEADERRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processHEADER_FOOTERRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processHIDE_OBJRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processHORIZONTAL_PAGE_BREAKRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processHYPERLINKRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processINDEXRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processINTERFACE_ENDRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processINTERFACE_HDRRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processITERATIONRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processLABELRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processLABEL_SSTRecord(Record theRecord) throws Exception {
			if (!isWithinWorkbookOrSheet()) {
				return;
			}
			LabelSSTRecord fRec = (LabelSSTRecord) theRecord;
			int row = fRec.getRow();
			short col = fRec.getColumn();
			String origVal = sstRecord.getString(fRec.getSSTIndex()).getString();

			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute(null, SpreadsheetConstants.ATTR_DATATYPE, SpreadsheetConstants.ATTR_DATATYPE, null, SpreadsheetConstants.ATTR_DATATYPE_STRING);
			atts.addAttribute(null, SpreadsheetConstants.ATTR_ROW, SpreadsheetConstants.ATTR_ROW, null, String.valueOf(row));
			atts.addAttribute(null, SpreadsheetConstants.ATTR_COL, SpreadsheetConstants.ATTR_COL, null, String.valueOf(col));
			atts.addAttribute(null, SpreadsheetConstants.ATTR_CELLREF, SpreadsheetConstants.ATTR_CELLREF, null, new CellReference(row, col).formatAsString(false));
			atts.addAttribute(null, SpreadsheetConstants.ATR_RAWVALUE, SpreadsheetConstants.ATR_RAWVALUE, null, String.valueOf(origVal));

			handler.startElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL, atts);
			handler.endElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL);
		}

		private void processLEFT_MARGINRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processLEGENDRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processLINE_FORMATRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processLINKED_DATARecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processMERGE_CELLSRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processMMSRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processMUL_BLANKRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processMUL_RKRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processNAMERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processNAME_COMMENTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processNOTERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processNUMBERRecord(Record theRecord) throws Exception {
			if (!isWithinWorkbookOrSheet()) {
				return;
			}
			//genericStartElement(theRecord);
			NumberRecord fRec = (NumberRecord) theRecord;
			int row = fRec.getRow();
			short col = fRec.getColumn();
			double origVal = fRec.getValue();

			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute(null, SpreadsheetConstants.ATTR_DATATYPE, SpreadsheetConstants.ATTR_DATATYPE, null, SpreadsheetConstants.ATTR_DATATYPE_NUMBER);
			atts.addAttribute(null, SpreadsheetConstants.ATTR_ROW, SpreadsheetConstants.ATTR_ROW, null, String.valueOf(row));
			atts.addAttribute(null, SpreadsheetConstants.ATTR_COL, SpreadsheetConstants.ATTR_COL, null, String.valueOf(col));
			atts.addAttribute(null, SpreadsheetConstants.ATTR_CELLREF, SpreadsheetConstants.ATTR_CELLREF, null, new CellReference(row, col).formatAsString(false));
			//atts.addAttribute(null, SpreadsheetConstants.ATR_RAWVALUE, SpreadsheetConstants.ATR_RAWVALUE, null, String.valueOf(origVal));
			atts.addAttribute(null, SpreadsheetConstants.ATTR_FINALVALUE, SpreadsheetConstants.ATTR_FINALVALUE, null, formatTrackingListener.formatNumberDateCell(fRec));

			handler.startElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL, atts);
			handler.endElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL);
		}

		private void processNUMBER_FORMAT_INDEXRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processOBJRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processOBJECT_LINKRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processOBJECT_PROTECTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processPAGE_ITEMRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processPALETTERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processPANERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processPASSWORDRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processPASSWORD_REV_4Record(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processPLOT_AREARecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processPLOT_GROWTHRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processPRECISIONRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processPRINT_GRIDLINESRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processPRINT_HEADERSRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processPRINT_SETUPRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processPROTECTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processPROTECTION_REV_4Record(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processRECALC_IDRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processREF_MODERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processREFRESH_ALLRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processRIGHT_MARGINRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processRKRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processROWRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSAVE_RECALCRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSCENARIO_PROTECTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSCLRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSELECTIONRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSERIESRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSERIES_CHART_GROUP_INDEXRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSERIES_INDEXRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSERIES_LABELSRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSERIES_LISTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSERIES_TEXTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSHARED_FORMULARecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSHEET_PROPERTIESRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSSTRecord(Record theRecord) throws Exception {
			sstRecord = (SSTRecord) theRecord;
		}

		private void processSTREAM_IDRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSTRINGRecord(Record theRecord) throws Exception {
			//genericStartEndElement(theRecord, true, true);
			StringRecord sRec = (StringRecord) theRecord;
			AttributesImpl atts = lastAttributes;
			lastAttributes = null;
			atts.addAttribute(null, SpreadsheetConstants.ATTR_FINALVALUE, SpreadsheetConstants.ATTR_FINALVALUE, null, sRec.getString());

			handler.startElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL, atts);
			handler.endElement(null, SpreadsheetConstants.TAG_CELL, SpreadsheetConstants.TAG_CELL);
		}

		private void processSTYLERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processSUP_BOOKRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processTAB_IDRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processTABLERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processTABLE_STYLESRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processTEXTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processTEXT_OBJECTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processTICKRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processTOP_MARGINRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processUNCALCEDRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processUNITSRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processUSE_SEL_FSRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processUSER_SVIEW_BEGINRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processUSER_SVIEW_ENDRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processV_CENTERRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processVALUE_RANGERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processVERTICAL_PAGE_BREAKRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processVIEW_DEFINITIONRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processVIEW_FIELDSRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processVIEW_SOURCERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processWINDOW_ONERecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processWINDOW_PROTECTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processWINDOW_TWORecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processWRITE_ACCESSRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processWRITE_PROTECTRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void processWS_BOOLRecord(Record theRecord) throws Exception {
			genericStartEndElement(theRecord, true, true);
		}

		private void genericStartEndElement(Record theRecord, boolean doStart, boolean doEnd) throws Exception {
			String tagName = theRecord.getClass().getSimpleName();
			if (doStart) {
				if (!McckUtil.isNullOrEmpty(theRecord.getGenericProperties())) {
					SortedMap<String, Supplier<?>> props = new TreeMap<>(theRecord.getGenericProperties());

					AttributesImpl atts = new AttributesImpl();
					for (Entry<String, Supplier<?>> ent : props.entrySet()) {
						Object val = ent.getValue().get();
						if (null != val) {
							if (val instanceof String || val instanceof Number) {
								atts.addAttribute(null, ent.getKey(), ent.getKey(), null, val.toString());
							} else if (val instanceof AnnotatedFlag) {
								AnnotatedFlag af = (AnnotatedFlag) val;
								String valStr = af.getClass().getSimpleName() + "[value=" + af.getValue().get() + ", description=" + af.getDescription() + "]";
								atts.addAttribute(null, ent.getKey(), ent.getKey(), null, valStr);
							} else {
								atts.addAttribute(null, ent.getKey(), ent.getKey(), null, val.toString());
							}
						}
					}

					//handler.startElement(null, tagName, tagName, atts);
				} else {
					//handler.startElement(null, tagName, tagName, new AttributesImpl());
				}
			}

			if (doEnd) {
				//handler.endElement(null, tagName, tagName);
			}
		}

		private void pushBof(BOFRecord theRecord) {
			bofStack.add(theRecord);
		}

		private BOFRecord popBof() {
			return bofStack.remove(bofStack.size() - 1);
		}

		private boolean isWithinWorkbookOrSheet() {
			BOFRecord brec = bofStack.get(bofStack.size() - 1);
			return (BOFRecord.TYPE_WORKBOOK == brec.getType() || BOFRecord.TYPE_WORKSHEET == brec.getType());
		}
	}

	@FunctionalInterface
	private interface HssfRecordProcessor {
		void process(Record theRecord) throws Exception;
	}
}
