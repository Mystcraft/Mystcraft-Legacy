package com.xcompwiz.mystcraft.words;

import java.util.HashMap;
import java.util.Random;

import com.xcompwiz.mystcraft.api.word.DrawableWord;

public class DrawableWordManager {

	private static HashMap<String, DrawableWord>	words	= new HashMap<String, DrawableWord>();

	public static void registerWord(String name, DrawableWord word) {
		if (name == null || word == null) return;
		if (words.containsKey(name)) return;
		words.put(name, word);
	}

	public static void registerWord(String name, Integer[] components) {
		if (name == null || components == null) return;
		registerWord(name, new DrawableWord(components));
	}

	public static DrawableWord getDrawableWord(String string) {
		if (string == null) return null;
		DrawableWord word = words.get(string);
		if (word == null) {
			word = new DrawableWord();
			Random rand = new Random(string.hashCode());
			int count = rand.nextInt(10) + 3;
			for (int i = 0; i < count; ++i) {
				word.components().add(rand.nextInt(20) + 4);
			}
			words.put(string, word);
		}
		return word;
	}
}
