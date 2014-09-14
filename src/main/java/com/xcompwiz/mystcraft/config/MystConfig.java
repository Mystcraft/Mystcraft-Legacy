package com.xcompwiz.mystcraft.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class MystConfig extends Configuration {

	public static final String	CATEGORY_TEXTURE		= "texture";
	public static final String	CATEGORY_ENTITY			= "entity";
	public static final String	CATEGORY_DEBUG			= "debug";
	public static final String	CATEGORY_SYMBOLS		= "symbol";
	public static final String	CATEGORY_INSTABILITY	= "instability";

	public MystConfig(File configfile) {
		super(configfile);
	}

	public int getOptional(String category, String key, int val) {
		String read = this.get(category, key, "").getString();
		if (read == null || read.length() == 0) return val;
		try {
			return Integer.parseInt(read);
		} catch (NumberFormatException e) {
			return val;
		}
	}

	public float getOptional(String category, String key, float val) {
		String read = this.get(category, key, "").getString();
		if (read == null || read.length() == 0) return val;
		try {
			return Float.parseFloat(read);
		} catch (NumberFormatException e) {
			return val;
		}
	}
}
