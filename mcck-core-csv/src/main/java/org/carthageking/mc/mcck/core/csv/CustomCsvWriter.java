package org.carthageking.mc.mcck.core.csv;

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
