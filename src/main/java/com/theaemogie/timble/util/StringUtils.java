package com.theaemogie.timble.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StringUtils {
	public static Path resourcePath(String resourcePath) {
		return Paths.get("src/main/resources/", resourcePath);
	}
	
	public static String removeQuotes(String input) {
		String output = input;
		if (output.indexOf('"') == 0 && output.lastIndexOf('"') == output.length() - 1)
			output = output.substring(1, output.length() - 1);
		return output;
	}
	
	public static String getExceptionAsString(Exception exception) {
		StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}
}