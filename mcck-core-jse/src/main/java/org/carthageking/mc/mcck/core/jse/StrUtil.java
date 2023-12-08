package org.carthageking.mc.mcck.core.jse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class StrUtil {

	private StrUtil() {
		// noop
	}

	public static List<String> readIntoLines(String str) {
		List<String> lst = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new StringReader(str))) {
			String line = null;
			while (null != (line = br.readLine())) {
				lst.add(line);
			}
		} catch (IOException e) {
			throw new McckException(e);
		}
		return lst;
	}
}
