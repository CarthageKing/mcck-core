package org.carthageking.mc.mcck.core.xml.sax;

/*-
 * #%L
 * mcck-core-xml
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

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.carthageking.mc.mcck.core.jse.McckIOUtil;
import org.carthageking.mc.mcck.core.jse.McckStrUtil;
import org.carthageking.mc.mcck.core.test.junit5.McckJunit5Util;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;

class PrintWriterContentHandlerTest {

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
	void test_patient_simplePrint() throws Exception {
		SAXParserFactory saxpf = SAXParserFactory.newInstance();
		SAXParser saxp = saxpf.newSAXParser();

		// include characters
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			try (InputStream is = getClass().getResourceAsStream("patient.xml")) {
				InputSource isrc = new InputSource(is);
				saxp.parse(isrc, new PrintWriterContentHandler(pw));
			}
			// System.err.println(sw);
			String exp = McckIOUtil.readAllAsStringFromClasspathResource(getClass(), "patient_out_includedChars.xml", StandardCharsets.UTF_8).trim();
			List<String> explst = McckStrUtil.readIntoLines(exp);
			List<String> actlst = McckStrUtil.readIntoLines(sw.toString());
			McckJunit5Util.assertSameLines(explst, actlst);
		}

		// exclude characters
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			try (InputStream is = getClass().getResourceAsStream("patient.xml")) {
				InputSource isrc = new InputSource(is);
				saxp.parse(isrc, new PrintWriterContentHandler(pw, false));
			}
			//System.err.println(sw);
			String exp = McckIOUtil.readAllAsStringFromClasspathResource(getClass(), "patient_out_noIncludedChars.xml", StandardCharsets.UTF_8).trim();
			List<String> explst = McckStrUtil.readIntoLines(exp);
			List<String> actlst = McckStrUtil.readIntoLines(sw.toString());
			McckJunit5Util.assertSameLines(explst, actlst);
		}

		// exclude characters and pretty print
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			try (InputStream is = getClass().getResourceAsStream("patient.xml")) {
				InputSource isrc = new InputSource(is);
				saxp.parse(isrc, new PrintWriterContentHandler(pw, false, true));
			}
			//System.err.println(sw);
			String exp = McckIOUtil.readAllAsStringFromClasspathResource(getClass(), "patient_out_noIncludedChars_prettyPrint.xml", StandardCharsets.UTF_8).trim();
			List<String> explst = McckStrUtil.readIntoLines(exp);
			List<String> actlst = McckStrUtil.readIntoLines(sw.toString());
			McckJunit5Util.assertSameLines(explst, actlst);
		}
	}
}
