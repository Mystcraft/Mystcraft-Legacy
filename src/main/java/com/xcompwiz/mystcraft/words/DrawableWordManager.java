package com.xcompwiz.mystcraft.words;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.xcompwiz.mystcraft.api.word.DrawableWord;
import com.xcompwiz.mystcraft.banners.BannerUtils;

public class DrawableWordManager {

	private static HashMap<String, DrawableWord> words = new HashMap<String, DrawableWord>();

	public static Map<String, DrawableWord> getWords() {
		return Collections.unmodifiableMap(words);
	}
	
	public static void registerWord(String name, DrawableWord word) {
		name = name.toLowerCase();
		if (name == null || word == null)
			return;
		if (words.containsKey(name))
			return;
		words.put(name, word);
		BannerUtils.addBasicPattern(name);
	}

	public static void registerWord(String name, Integer[] components) {
		if (name == null || components == null)
			return;
		registerWord(name, new DrawableWord(components));
	}

	public static DrawableWord getDrawableWord(String string) {
		if (string == null)
			return null;
		string = string.toLowerCase();
		DrawableWord word = words.get(string);
		if (word == null) {
			word = new DrawableWord();
			Random rand = new Random(string.hashCode());
			int maxcompindex = 20;
			int count = rand.nextInt(10) + 3;
			if (string.startsWith("easter")) { //TODO: This is a silly hack
				count = 4;
				maxcompindex = 8;
			}
			for (int i = 0; i < count; ++i) {
				word.components().add(rand.nextInt(maxcompindex) + 4);
			}
			words.put(string, word);
		}
		return word;
	}
}
