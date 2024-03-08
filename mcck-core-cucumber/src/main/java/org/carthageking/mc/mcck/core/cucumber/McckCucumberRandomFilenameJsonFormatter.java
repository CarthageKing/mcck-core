package org.carthageking.mc.mcck.core.cucumber;

/*-
 * #%L
 * mcck-core-cucumber
 * %%
 * Copyright (C) 2024 Michael I. Calderero
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

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import org.carthageking.mc.mcck.core.jse.McckException;
import org.carthageking.mc.mcck.core.jse.McckIOUtil;

import io.cucumber.core.plugin.JsonFormatter;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestRunFinished;

public class McckCucumberRandomFilenameJsonFormatter implements EventListener, Closeable, AutoCloseable {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(McckCucumberRandomFilenameJsonFormatter.class);

	private final File outFile;
	private final OutputStream outFileOS;
	private final JsonFormatter delegate;

	public McckCucumberRandomFilenameJsonFormatter(String outFilePathStr) {
		File f = new File(outFilePathStr);
		File parentFile = f.getParentFile();
		String filename = f.getName();
		int idx = filename.lastIndexOf('.');
		String randomization = UUID.randomUUID().toString();
		String baseFilename = null;
		String fileExtension = "";

		if (0 == idx) {
			// file is something like .json
			throw new IllegalArgumentException("Cucumber JSON formatter cannot accept hidden file path: " + outFilePathStr);
		} else if (idx < 0) {
			// file is something like cucumber
			baseFilename = filename;
			fileExtension = "";
		} else {
			if (idx == filename.length() - 1) {
				// file is something like cucumber.
				throw new IllegalArgumentException("Cucumber JSON formatter cannot accept filename ending with '.': " + outFilePathStr);
			} else {
				// file is something like cucumber.json
				baseFilename = filename.substring(0, idx);
				fileExtension = filename.substring(idx + 1);
			}
		}

		if (fileExtension.isEmpty()) {
			outFile = new File(parentFile, baseFilename + "-" + randomization);
		} else {
			outFile = new File(parentFile, baseFilename + "-" + randomization + "." + fileExtension);
		}

		if (null != parentFile) {
			parentFile.mkdirs();
		}

		try {
			outFileOS = new FileOutputStream(outFile);
		} catch (FileNotFoundException e) {
			throw new McckException("Error creating file " + outFile, e);
		}

		delegate = new JsonFormatter(outFileOS);
	}

	// for unit tests only
	File getOutFile() {
		return outFile;
	}

	@Override
	public void setEventPublisher(EventPublisher publisher) {
		delegate.setEventPublisher(publisher);
		// also register our own to close ourselves
		publisher.registerHandlerFor(TestRunFinished.class, this::doClose);
	}

	private void doClose(TestRunFinished event) {
		try {
			close();
		} catch (IOException e) {
			// shouldn't happen
			throw new McckException(e);
		}
	}

	@Override
	public void close() throws IOException {
		McckIOUtil.flushFully(outFileOS, e -> LOG.warn("Encountered error while flushing", e));
		McckIOUtil.closeFully(outFileOS, e -> LOG.warn("Encountered error while closing", e));
	}
}
