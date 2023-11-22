package org.carthageking.mc.mcck.core.xml.sax;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.carthageking.mc.mcck.core.jse.IOUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
			String exp = IOUtil.readAllAsStringFromClasspathResource(getClass(), "patient_out_includedChars.xml", StandardCharsets.UTF_8).trim();
			Assertions.assertEquals(exp, sw.toString().trim());
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
			String exp = IOUtil.readAllAsStringFromClasspathResource(getClass(), "patient_out_noIncludedChars.xml", StandardCharsets.UTF_8).trim();
			Assertions.assertEquals(exp, sw.toString().trim());
		}
	}
}
