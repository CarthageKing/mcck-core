package org.carthageking.mc.mcck.core.spreadsheet.excel.impl;

/*-
 * #%L
 * mcck-core-spreadsheet-excel
 * %%
 * Copyright (C) 2023 - 2024 Michael I. Calderero
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.carthageking.mc.mcck.core.jse.McckIOUtil;
import org.carthageking.mc.mcck.core.jse.McckStrUtil;
import org.carthageking.mc.mcck.core.test.junit5.McckJunit5Util;
import org.carthageking.mc.mcck.core.xml.sax.PrintWriterContentHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Excel2007ReaderTest {

	private static final SAXParserFactory SAX_PARSER_FACTORY = SAXParserFactory.newInstance();

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

		try (InputStream is = getClass().getResourceAsStream("ExampleExcel.xlsx")) {
			Excel2007Reader reader = new Excel2007Reader(SAX_PARSER_FACTORY);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			reader.readSpreadsheet(is, new PrintWriterContentHandler(pw, false, true));
			pw.flush();
			//System.out.println(sw);
			actual = sw.toString();
		}

		expected = McckIOUtil.readAllAsStringFromClasspathResource(getClass(), "ExampleExcel_xls_output01.xml", StandardCharsets.UTF_8);
		actlst = McckStrUtil.readIntoLines(actual);
		explst = McckStrUtil.readIntoLines(expected);
		McckJunit5Util.assertSameLines(explst, actlst);

		{
			File toRead = new File("src/test/resources/org/carthageking/mc/mcck/core/spreadsheet/excel/impl/ExampleExcel.xlsx");
			Excel2007Reader reader = new Excel2007Reader(SAX_PARSER_FACTORY);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			reader.readSpreadsheet(toRead, new PrintWriterContentHandler(pw, false, true));
			pw.flush();
			//System.out.println(sw);
			actual = sw.toString();
		}

		expected = McckIOUtil.readAllAsStringFromClasspathResource(getClass(), "ExampleExcel_xls_output01.xml", StandardCharsets.UTF_8);
		actlst = McckStrUtil.readIntoLines(actual);
		explst = McckStrUtil.readIntoLines(expected);
		McckJunit5Util.assertSameLines(explst, actlst);
	}
}
