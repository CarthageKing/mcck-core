package org.carthageking.mc.mcck.core.jse;

/*-
 * #%L
 * mcck-core-jse
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

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public final class McckIOUtil {

	private static final int DEFAULT_BUFFER_SIZE = 1024;

	private McckIOUtil() {
		// noop
	}

	public static String readAllAsStringFromClasspathResource(Class<?> refClazz, String classpath, Charset charset) throws IOException {
		try (InputStream is = refClazz.getResourceAsStream(classpath)) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			is.transferTo(bos);
			return new String(bos.toByteArray(), charset);
		}
	}

	public static void closeFully(AutoCloseable c) {
		closeFully(c, null);
	}

	public static void closeFully(AutoCloseable c, Consumer<Exception> exceptionHandler) {
		if (null != c) {
			try {
				c.close();
			} catch (Exception e) {
				if (null != exceptionHandler) {
					exceptionHandler.accept(e);
				}
			}
		}
	}

	public static void closeFully(Closeable c) {
		closeFully(c, null);
	}

	public static void closeFully(Closeable c, Consumer<Exception> exceptionHandler) {
		if (null != c) {
			try {
				c.close();
			} catch (Exception e) {
				if (null != exceptionHandler) {
					exceptionHandler.accept(e);
				}
			}
		}
	}

	public static String readAllAsString(InputStream is, Charset charset) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
		is.transferTo(bos);
		return bos.toString(charset);
	}
}
