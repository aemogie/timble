package com.theaemogie.timble.util;

import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Aemogie.
 * <br><br>
 * Method name structure: {@code get}<i>Type</i>{@code Value_}<i>FIRST_SPLIT_KEY</i><i>SECOND_SPLIT_KEY</i><br>
 * Example: {@code getStringValue_KC}<br><br>
 * Split keys:<br>
 * F - First (First split.)<br>
 * K - Key (First split but closed in quotation marks.)<br>
 * S - Split (Second split.)<br>
 * C - Comma (Comma as second split.)<br>
 */
public class JsonParser {
	
	/**
	 * Splits the {@code source} first at the {@code firstSplit} into 2.
	 * <br>Then from the second part, where the value <i>should</i> be, it splits again at the first occurrence of {@code secondSplit}.
	 * <br>Prints an error to {@link System#err} and exits, if {@code compulsory} is true and {@link ArrayIndexOutOfBoundsException} is thrown, when splitting with {@code firstSplit}.
	 *
	 * @param source      The json text to split.
	 * @param firstSplit  The first split. The part before the desired result. Usually the key.
	 * @param secondSplit The second split. The part after the desired value. Usually a {@code ,} (comma).
	 * @param compulsory  Whether to throw an error if the split wasn't possible.
	 * @return A String of the value between {@code firstSplit} and {@code secondSplit} or an empty string if the split wasn't possible.
	 */
	private static String getValue_FS(String source, @Language("RegExp") String firstSplit, @Language("RegExp") String secondSplit, boolean compulsory) {
		try {
			return source.split(firstSplit, 2)[1].split(secondSplit, 2)[0].trim();
		} catch (ArrayIndexOutOfBoundsException ignored) {
			if (compulsory) {
				System.err.println("Invalid key: \"" + firstSplit + "\" not present in: \n\t" + source);
				System.exit(-1);
			}
			return "";
		}
	}
	
	/**
	 * @param key Same as {@link JsonParser#getValue_FS(String, String, String, boolean)} but uses a Key for the first split.
	 */
	private static String getValue_KS(String source, String key, @Language("RegExp") String secondSplit, boolean compulsory) {
		return getValue_FS(source, "\"" + key + "\\s*\":", secondSplit, compulsory);
	}
	
	/**
	 * @return Same as {@link JsonParser#getValue_KS(String, String, String, boolean)}, but removes the start and end quotation marks from the value.
	 */
	public static String getStringValue_KS(String source, String key, @Language("RegExp") String secondSplit, boolean compulsory) {
		return StringUtils.removeQuotes(
				getValue_KS(source, key, secondSplit, compulsory).trim()
		).trim();
	}
	
	/**
	 * @return Same as {@link JsonParser#getStringValue_KS(String, String, String, boolean)}, but automatically splits the value at the first comma.
	 */
	public static String getStringValue_KC(String source, String key, boolean compulsory) {
		return getStringValue_KS(source, key, ",", compulsory);
	}
	
	
	public static int getIntegerValue(String source, String key, boolean compulsory) {
		try {
			return Integer.parseInt(getStringValue_KC(source, key, compulsory));
		} catch (NumberFormatException ignored) {
			
			return 0;
		}
	}
	
	public static float getFloatValue(String source, String key, boolean compulsory) {
		return Float.parseFloat(getStringValue_KC(source, key, compulsory));
	}
	
	public static double getDoubleValue(String source, String key, boolean compulsory) {
		return Double.parseDouble(getStringValue_KC(source, key, compulsory));
	}
	
	public static boolean getBooleanValue(String source, String key, boolean compulsory) {
		return Boolean.parseBoolean(getStringValue_KC(source, key, compulsory));
	}
	
	public static String[] getArrayValue(String source, String key) {
		List<String> outputList;
		outputList = Arrays.asList(source.split("\"" + key + "\":\\s*\\[")[1].split("]")[0].split(","));
		List<String> outputListTrimmed = new ArrayList<>();
		outputList.forEach(str -> outputListTrimmed.add(str.trim()));
		return outputListTrimmed.toArray(new String[0]);
	}
	
	public static String[] getArrayValue(String source, String key, String nextKey, @Language("RegExp") String splitRegex) {
		List<String> outputList = Arrays.asList(getValue_KS(source, key, "\\s\"" + nextKey + "\"", true).split(splitRegex));
		List<String> outputListTrimmed = new ArrayList<>();
		outputList.forEach(str -> outputListTrimmed.add(str.trim()));
		return outputListTrimmed.toArray(new String[0]);
	}
}