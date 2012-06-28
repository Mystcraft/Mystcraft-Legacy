package com.xcompwiz.mystcraft.api;

import com.xcompwiz.mystcraft.api.internal.IDimensionAPI;
import com.xcompwiz.mystcraft.api.internal.IGrammarAPI;
import com.xcompwiz.mystcraft.api.internal.IRenderAPI;
import com.xcompwiz.mystcraft.api.internal.IWordAPI;

import cpw.mods.fml.common.Loader;

/**
 * This class provides interface implementations for Mystcraft functionality for external use. This is the entry point
 * for the API. Use the static function in this class to get your own instance of the API. From here you have access to
 * implementations of the different parts of the API.
 * 
 * @author XCompWiz
 */
public abstract class MystAPI {
	/** You are responsible for verifying that Mystcraft is available before calling this function. */
	public static MystAPI getInstance() {
		IMystAPIProvider mystcraft = (IMystAPIProvider) Loader.instance().getIndexedModList().get(MystObjects.MystcraftModId).getMod();
		return mystcraft.getAPIInstance();
	}

	/** This is just used so that Mystcraft can provide the API instance */
	public interface IMystAPIProvider {
		MystAPI getAPIInstance();
	}

	public IDimensionAPI	dimension;
	public IGrammarAPI		grammar;
	public IWordAPI			word;

	public IRenderAPI		render;
}
