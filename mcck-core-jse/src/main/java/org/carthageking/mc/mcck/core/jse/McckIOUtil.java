package org.carthageking.mc.mcck.core.jse;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public final class McckIOUtil {

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
}
