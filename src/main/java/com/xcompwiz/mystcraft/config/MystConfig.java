package com.xcompwiz.mystcraft.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class MystConfig extends Configuration {

	public static final String	CATEGORY_TEXTURE		= "texture";
	public static final String	CATEGORY_ENTITY			= "entity";
	public static final String	CATEGORY_DEBUG			= "debug";
	public static final String	CATEGORY_SYMBOLS		= "symbol";
	public static final String	CATEGORY_INSTABILITY	= "instability";
	public static final String	CATEGORY_BALANCE		= "balance";
	private boolean				allowoptional			= true;

	public MystConfig(File configfile) {
		super(configfile);
	}

	public void setAllowOptional(boolean b) {
		allowoptional = b;
	}

	public int getOptional(String category, String key, int val) {
		if (allowoptional) {
			String read = this.get(category, key, "").getString();
			if (read == null || read.length() == 0) return val;
			try {
				return Integer.parseInt(read);
			} catch (NumberFormatException e) {
			}
			return val;
		}
		return get(category, key, val).getInt();
	}

	public float getOptional(String category, String key, float val) {
		if (allowoptional) {
			String read = this.get(category, key, "").getString();
			if (read == null || read.length() == 0) return val;
			try {
				return Float.parseFloat(read);
			} catch (NumberFormatException e) {
			}
			return val;
		}
		return (float) get(category, key, val).getDouble();
	}

	public boolean getOptional(String category, String key, boolean val) {
		if (allowoptional) {
			String read = this.get(category, key, "").getString();
			if (read == null || read.length() == 0) return val;
			try {
				return Boolean.parseBoolean(read);
			} catch (NumberFormatException e) {
				return val;
			}
		}
		return get(category, key, val).getBoolean();
	}
}
