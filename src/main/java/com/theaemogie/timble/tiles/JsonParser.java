package com.theaemogie.timble.tiles;

import org.intellij.lang.annotations.RegExp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Aemogie.
 */
public class JsonParser {
	private static String getValue(String source, String key) {
		return source.split("\"" + key + "\\s*\":")[1].split(",")[0].trim();
	}
	private static String getValue_CustomSplit(String source, String key, String splitRegex) {
		return source.split("\"" + key + "\\s*\":")[1].split(splitRegex)[0].trim();
	}
	
	private static String getValue(String source, String key, String nextKey) {
		return source.split("\"" + key + "\\s*\":")[1].split(",\\s*\"" + nextKey + "\"")[0].trim();
	}
	
	public static String getStringValue(String source, String key) {
		return getValue(source, key).replace("\"", "").trim();
	}
	
	private static String getStringValue(String source, String key, String nextKey) {
		return getValue(source, key, nextKey).replace("\"", "").trim();
	}
	
	public static int getIntegerValue(String source, String key) {
		return Integer.parseInt(getStringValue(source, key));
	}
	
	public static float getFloatValue(String source, String key) {
		return Float.parseFloat(getStringValue(source, key));
	}
	
	public static double getDoubleValue(String source, String key) {
		return Double.parseDouble(getStringValue(source, key));
	}
	
	public static boolean getBooleanValue(String source, String key) {
		return Boolean.parseBoolean(getStringValue(source, key));
	}
	
	public static String[] getArrayValue(String source, String key) {
		List<String> outputList = Arrays.asList(source.split("\"" + key + "\":\\s*\\[")[1].split("]")[0].split(","));
		List<String> outputListTrimmed = new ArrayList<>();
		outputList.forEach(str -> outputListTrimmed.add(str.trim()));
		return outputListTrimmed.toArray(new String[0]);
	}
	
	public static String[] getArrayValue(String source, String key, String nextKey, @RegExp String splitRegex) {
		List<String> outputList = Arrays.asList(getValue(source, key, nextKey).split(splitRegex));
		List<String> outputListTrimmed = new ArrayList<>();
		outputList.forEach(str -> outputListTrimmed.add(str.trim()));
		return outputListTrimmed.toArray(new String[0]);
	}
}