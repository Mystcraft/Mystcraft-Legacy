package com.xcompwiz.mystcraft.api;

import com.xcompwiz.mystcraft.api.client.IRenderAPI;
import com.xcompwiz.mystcraft.api.grammar.IGrammarAPI;
import com.xcompwiz.mystcraft.api.linking.IDimensionAPI;
import com.xcompwiz.mystcraft.api.linking.ILinkingAPI;
import com.xcompwiz.mystcraft.api.word.IWordAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// In order to get an instance of this class, send an IMC message to Mystcraft with the key "register" and a string value which is a method (with classpath)
// which takes an instance of MystAPI as it's only param.
// Example: FMLInterModComms.sendMessage("Mystcraft", "register", "com.xcompwiz.newmod.addons.mystcraft.register");

/**
 * This class provides interface implementations for Mystcraft functionality for external use. This is the entry point for the API. From here you have access to
 * implementations of the different parts of the API.
 * @author XCompWiz
 */
public abstract class MystAPI {
	public abstract IDimensionAPI getDimensionAPI();

	public abstract ILinkingAPI getLinkingAPI();

	public abstract IGrammarAPI getGrammarAPI();

	public abstract IWordAPI getWordAPI();

	@SideOnly(Side.CLIENT)
	public abstract IRenderAPI getRenderAPI();
}
