package org.carthageking.mc.mcck.core.csv;

/*-
 * #%L
 * mcck-core-csv
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

import java.io.StringWriter;
import java.util.Arrays;

import org.apache.commons.csv.CSVFormat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CustomCsvWriterTest {

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
	void test() throws Exception {
		StringWriter sw = new StringWriter();

		CSVFormat csvFormat = CSVFormat.EXCEL;
		char separatorChar = csvFormat.getDelimiterString().charAt(0);
		char quoteChar = csvFormat.getQuoteCharacter();
		char escapeChar = csvFormat.getQuoteCharacter();
		String lineEnd = csvFormat.getRecordSeparator();

		try (CustomCsvWriter writer = new CustomCsvWriter(sw, separatorChar, quoteChar, escapeChar, lineEnd)) {
			writer.writeNext(Arrays.asList("123", "a number 1", "who said \"I am sam\"?", "O'Leary, wher\"\"e did you go?", "Well\\come! \"Heathen''"));
			writer.flush();
		}

		// System.err.println(sw);
		// Files.write(new File("target/out.csv").toPath(),
		// sw.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
		Assertions.assertEquals("\"123\",\"a number 1\",\"who said \"\"I am sam\"\"?\",\"O'Leary, wher\"\"\"\"e did you go?\",\"Well\\come! \"\"Heathen''\"", sw.toString().trim());
	}
}
