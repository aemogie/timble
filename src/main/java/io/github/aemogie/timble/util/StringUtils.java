package io.github.aemogie.timble.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {
	public static Path resourcePath(String... resourcePath) {
		return Paths.get("src/main/resources", resourcePath);
	}
	
	public static List<String> removeEmptyEntrees(List<String> strings) {
		return strings.stream().filter(s -> !s.trim().equals("")).collect(Collectors.toList());
	}
	
	public static String[] removeEmptyEntrees(String... strings) {
		return removeEmptyEntrees(Arrays.asList(strings)).toArray(new String[0]);
	}
}