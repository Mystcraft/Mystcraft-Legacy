package com.xcompwiz.mystcraft.api.hook;

import com.xcompwiz.mystcraft.api.APIInstanceProvider;
import com.xcompwiz.mystcraft.api.word.DrawableWord;

/**
 * Provides functions for interacting with the words system. You can use this to create your own Narayan words (See WordData for examples). The implementation
 * of this is provided by {@link APIInstanceProvider} as "word-1". Do NOT implement this yourself!
 * @author xcompwiz
 */
public interface WordAPI {

	/**
	 * Binds a DrawableWord to the provided name key if no other word is already bound to that name. Use this to make your custom words render correctly on your
	 * symbols.
	 */
	public void registerWord(String name, DrawableWord word);

	/**
	 * Constructs a DrawableWord from the provided component array and binds it to the provided name key if no other word is already bound to that name. Use
	 * this to make your custom words render correctly on your symbols.
	 */
	public void registerWord(String name, Integer[] components);

}
