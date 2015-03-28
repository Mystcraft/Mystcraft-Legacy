package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.config.MystConfig;

public class ModLinkEffects {
	private static MystConfig	config	= null;

	public static boolean isPropertyAllowed(String property) {
		String key = "crafting.linkeffects." + property.toLowerCase().replace(' ', '_') + ".enabled";
		boolean val = config.get(MystConfig.CATEGORY_GENERAL, key, true).getBoolean(true);
		if (config.hasChanged()) config.save();
		return val;
	}

	public static void setConfigs(MystConfig c) {
		config = c;
	}

}
