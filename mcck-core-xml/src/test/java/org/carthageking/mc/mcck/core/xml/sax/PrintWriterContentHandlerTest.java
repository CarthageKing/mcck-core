package org.carthageking.mc.mcck.core.xml.sax;

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
