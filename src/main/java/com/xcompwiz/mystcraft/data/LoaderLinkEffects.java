package com.xcompwiz.mystcraft.data;

import java.util.HashMap;

import com.xcompwiz.mystcraft.config.MystConfig;

public class LoaderLinkEffects {
	private static MystConfig				config	= null;

	public static HashMap<String, Boolean>	allowed	= new HashMap<String, Boolean>();

	public static boolean isPropertyAllowed(String property) {
		String key = "crafting.linkeffects." + property.toLowerCase().replace(' ', '_') + ".enabled";
		boolean val = config.get(MystConfig.CATEGORY_GENERAL, key, true).getBoolean(true);
		if (config.hasChanged()) config.save();
		return val;
	}

	public static void setConfig(MystConfig c) {
		config = c;
	}

}
