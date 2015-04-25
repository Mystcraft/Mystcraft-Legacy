package com.xcompwiz.mystcraft.api.impl.word;

import com.xcompwiz.mystcraft.api.hook.WordAPI;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.word.DrawableWord;

public class WordAPIWrapper extends APIWrapper implements WordAPI {

	public WordAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public void registerWord(String name, DrawableWord word) {
		InternalAPI.word.registerWord(name, word);
	}

	@Override
	public void registerWord(String name, Integer[] components) {
		InternalAPI.word.registerWord(name, components);
	}

}
