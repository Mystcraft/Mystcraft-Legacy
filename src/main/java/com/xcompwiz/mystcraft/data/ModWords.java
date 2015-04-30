package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.impl.word.WordAPIWrapper;
import com.xcompwiz.mystcraft.api.word.WordData;

public class ModWords {

	public static void initialize() {
		WordData.init(new WordAPIWrapper("Mystcraft")); //TODO: This should use the API Provider 
		for (int i = 0; i < 26; ++i) {
			InternalAPI.word.registerWord("" + i, constructNumber(i));
		}
	}

	private static Integer[] constructNumber(int num) {
		int first = 0;
		if (num == 0) return new Integer[] { 1 };
		else if (num >= 25) return new Integer[] { 2 };
		else if (num >= 20) first = 63;
		else if (num >= 15) first = 62;
		else if (num >= 10) first = 61;
		else if (num >= 5) first = 60;
		int second = 0;
		if (num % 5 > 0) {
			second = num % 5 + 55;
		}
		if (first > 0) {
			if (second > 0) return new Integer[] { first, second };
			return new Integer[] { first };
		}
		return new Integer[] { second };
	}

}
