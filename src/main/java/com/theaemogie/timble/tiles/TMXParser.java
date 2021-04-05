package com.theaemogie.timble.tiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class TMXParser {
	public static ArrayList<ArrayList<Integer>> parseJson(String filePath) {
		
		String source = "";
		
		try {
			source = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> splitStage1 = new ArrayList<>(Arrays.asList(Arrays.asList(source.split("[],\\s\\n]*(\"nextlayerid\"|\"layers\"):\\[*")).get(1).trim().split("\\{|},")));
		
		for (int i = 0; i < splitStage1.size(); i += 2) {
			splitStage1.remove(i);
			i--;
		}
		
		ArrayList<String> splitStage2 = new ArrayList<>();
		
		for (String layer : splitStage1) {
			splitStage2.add(layer.split("\"data\":\\[|]")[1]);
		}
		
		ArrayList<ArrayList<Integer>> output = new ArrayList<>();
		
		for (String layer : splitStage2) {
			ArrayList<String> firstDimString = new ArrayList<>(Arrays.asList(layer.split(",")));
			ArrayList<Integer> firstDim = new ArrayList<>();
			firstDimString.forEach(val -> firstDim.add(Integer.parseInt(val.trim())));
			output.add(firstDim);
		}
		
		return output;
	}
}
