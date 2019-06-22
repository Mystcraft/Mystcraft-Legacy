package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.config.MystConfig;

public class ModRecipes {

	private static boolean Linkbook;

	public static void loadConfigs(MystConfig config) {
		Linkbook = config.get(MystConfig.CATEGORY_GENERAL, "crafting.linkbook.enabled", true).getBoolean(true);
	}

	public static void addRecipes() {
		// Linking Book
		if (Linkbook) {
			ModRegistryPrimer.queueForRegistration(new RecipeLinkingbook());
		}
	}
}
