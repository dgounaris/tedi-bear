package server.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Distance {

	public static int Hamming(String s1, String s2) {
		
		if (s1 == null || s2 == null) {
			return 0;
		}
		
		char[] s1Array = s1.toLowerCase().toCharArray();
		char[] s2Array = s2.toLowerCase().toCharArray();
		int differences = Math.abs(s1Array.length-s2Array.length);
		int size;
		if (s1Array.length > s2Array.length) {
			size = s2Array.length;
		}
		else {
			size = s1Array.length;
		}
		for (int i=0;i<size;i++) {
			if (s1Array[i] != s2Array[i]) {
				differences++;
			}
		}
		return differences;
		
	}
	
	public static int Levenshtein(String s1, String s2) {
		
		if ( (s1 == null) || (s2 == null) ) return 0;
		if ( (s1.length() == 0) || (s2.length() == 0) ) return 0;
		if ( s1.equals(s2) ) return 0;
		
		int s1WordCount = s1.length();
		int s2WordCount = s2.length();
		
		char[] s1Array = s1.toLowerCase().toCharArray();
		char[] s2Array = s2.toLowerCase().toCharArray();
		
		int[][] distance = new int[s1WordCount+1][s2WordCount+1];
		for (int i=0; i<= s1WordCount; i++) {
			distance[i][0] = i;
		}
		for (int i=0; i<= s2WordCount; i++) {
			distance[0][i] = i;
		}
		
		for (int i=1; i <= s1WordCount; i++) {
			for (int j=1; j <= s2WordCount; j++) {
				int cost = (s1Array[i-1] == s2Array[j-1]) ? 0:1;
				distance[i][j] = Math.min(Math.min(distance[i-1][j] + 1, distance[i][j-1] + 1), distance[i-1][j-1] + cost);
			}
		}
		
		return distance[s1WordCount][s2WordCount];
	}
	
	public static int CommonWords(String s1, String s2) {
		
		List<String> s1Words = Arrays.asList(s1.toLowerCase().split("\\s+"));
		List<String> s2Words = Arrays.asList(s2.toLowerCase().split("\\s+"));
		int countCommon = 0;
		for (String s1Word : s1Words) {
			if (s2Words.contains(s1Word)) {
				countCommon++;
			}
		}
		return countCommon;
	}
	
}
