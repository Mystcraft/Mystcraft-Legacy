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

	private static String objToString(Object obj) {
		if (obj == null) return "";
		return obj.toString();
	}

	private static Integer toInteger(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
