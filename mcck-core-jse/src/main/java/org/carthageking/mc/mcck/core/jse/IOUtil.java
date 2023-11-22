package org.carthageking.mc.mcck.core.jse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public final class IOUtil {

	private IOUtil() {
		// noop
	}

	public static String readAllAsStringFromClasspathResource(Class<?> refClazz, String classpath, Charset charset) throws IOException {
		try (InputStream is = refClazz.getResourceAsStream(classpath)) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			is.transferTo(bos);
			return new String(bos.toByteArray(), charset);
		}
	}
}
