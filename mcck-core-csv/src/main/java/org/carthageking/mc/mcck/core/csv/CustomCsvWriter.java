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

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import com.opencsv.CSVWriter;

public class CustomCsvWriter extends CSVWriter {

	public CustomCsvWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd) {
		super(writer, separator, quotechar, escapechar, lineEnd);
	}

	public void writeNext(Collection<String> nextLine) {
		writeNext(nextLine, true);
	}

	public void writeNext(Collection<String> nextLine, boolean applyQuotesToAll) {
		try {
			writeNext(nextLine, applyQuotesToAll, new StringBuilder(INITIAL_STRING_SIZE));
		} catch (IOException e) {
			exception = e;
		}
	}

	protected void writeNext(Collection<String> nextLine, boolean applyQuotesToAll, Appendable appendable) throws IOException {
		if (nextLine == null) {
			return;
		}

		int counter = -1;
		for (String nextElement : nextLine) {
			counter++;

			if (counter != 0) {
				appendable.append(separator);
			}

			if (nextElement == null) {
				continue;
			}

			boolean stringContainsSpecialCharacters = stringContainsSpecialCharacters(nextElement);

			appendQuoteCharacterIfNeeded2(applyQuotesToAll, appendable, stringContainsSpecialCharacters);

			if (stringContainsSpecialCharacters) {
				processLine(nextElement, appendable);
			} else {
				appendable.append(nextElement);
			}

			appendQuoteCharacterIfNeeded2(applyQuotesToAll, appendable, stringContainsSpecialCharacters);
		}

		appendable.append(lineEnd);
		writer.write(appendable.toString());
	}

	private void appendQuoteCharacterIfNeeded2(boolean applyQuotesToAll, Appendable appendable, Boolean stringContainsSpecialCharacters) throws IOException {
		if ((applyQuotesToAll || stringContainsSpecialCharacters) && quotechar != NO_QUOTE_CHARACTER) {
			appendable.append(quotechar);
		}
	}
}
