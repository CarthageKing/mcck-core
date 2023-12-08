package org.carthageking.mc.mcck.core.spreadsheet.excel.impl;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.carthageking.mc.mcck.core.jse.IOUtil;
import org.carthageking.mc.mcck.core.jse.StrUtil;
import org.carthageking.mc.mcck.core.test.junit5.McckJunit5Util;
import org.carthageking.mc.mcck.core.xml.sax.PrintWriterContentHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Excel97To2003ReaderTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test_readSpreadsheet_ok01() throws Exception {
		String actual = null;
		String expected = null;
		List<String> actlst = null;
		List<String> explst = null;

		try (InputStream is = getClass().getResourceAsStream("ExampleExcel.xls")) {
			Excel97To2003Reader reader = new Excel97To2003Reader();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			reader.readSpreadsheet(is, new PrintWriterContentHandler(pw, false, true));
			//System.out.println(sw);
			actual = sw.toString();
		}

		expected = IOUtil.readAllAsStringFromClasspathResource(getClass(), "ExampleExcel_xls_output01.xml", StandardCharsets.UTF_8);
		actlst = StrUtil.readIntoLines(actual);
		explst = StrUtil.readIntoLines(expected);
		McckJunit5Util.assertSameLines(explst, actlst);

		{
			File toRead = new File("src/test/resources/org/carthageking/mc/mcck/core/spreadsheet/excel/impl/ExampleExcel.xls");
			Excel97To2003Reader reader = new Excel97To2003Reader();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			reader.readSpreadsheet(toRead, new PrintWriterContentHandler(pw, false, true));
			//System.out.println(sw);
			actual = sw.toString();
		}

		expected = IOUtil.readAllAsStringFromClasspathResource(getClass(), "ExampleExcel_xls_output01.xml", StandardCharsets.UTF_8);
		actlst = StrUtil.readIntoLines(actual);
		explst = StrUtil.readIntoLines(expected);
		McckJunit5Util.assertSameLines(explst, actlst);
	}
}
