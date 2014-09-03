package com.xcompwiz.mystcraft.world.agedata;

import java.lang.reflect.Method;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;

import com.xcompwiz.mystcraft.instability.InstabilityManager;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.world.agedata.AgeData.AgeDataData;

public class AgeDataLoaderManager {

	public static abstract class AgeDataLoader {
		public abstract Object load(NBTTagCompound nbt);
	}

	private static final String	currentversion	= "4.2";

	private static HashMap<String, AgeDataLoader> loaders = new HashMap<String, AgeDataLoader>();

	static {
		loaders.put("4.2", new AgeDataLoaderV4_2());
		loaders.put("4.1", new AgeDataLoaderV4_1());
		loaders.put("4.0", new AgeDataLoaderV4());
		loaders.put("", new AgeDataLegacy());
	}

	public static AgeDataData load(NBTTagCompound nbt) {
		String version = nbt.getString("Version");
		AgeDataLoader loader = loaders.get(version);
		Object obj = loader.load(nbt);

		AgeDataData data = convert(obj);

		update(data);
		return data;
	}

	private static AgeDataData convert(Object obj) {
		Class<?>[] ctorArgClasses = new Class<?>[1];
		ctorArgClasses[0] = obj.getClass();
		try {
			// TODO: Bleck.  This is icky.  Replace this.
			Method method = AgeDataLoaderManager.class.getMethod("convert", ctorArgClasses);
			return (AgeDataData) method.invoke(null, obj);
		} catch (Exception e) {
			LoggerUtils.error("Caught an exception during agedata loading");
			LoggerUtils.error(e.toString());
			throw new RuntimeException("Error when converting agedata storage object to current version.", e);
		}
	}

	//TODO: Make solver to handle updating
	private static void update(AgeDataData data) {
		if (data.version.equals("legacy")) {
			data.version = "4.0";
		}
		// Update 4.0 -> 4.1
		if (data.version.equals("4.0")) {
			data.pages.add(0, Page.createLinkPage());
			data.version = "4.1";
		}

		// Update 4.1 -> 4.2
		if (data.version.equals("4.1")) {
			//List<String> data.effects;
			for (String deckname : InstabilityManager.getDecks()) {
				//FIXME: !Prioritize old effects?
			}
			data.version = "4.2";
		}

		if (data.agename.isEmpty()) {
			data.agename = "Unnamed Age";
		}
		if (!data.version.equals(currentversion)) {
			throw new RuntimeException("Error updating old agedata file! Version is " + data.version);
		}
	}

	public static AgeDataData convert(AgeDataLoaderV4_2.AgeDataData extern) {
		AgeDataData data = new AgeDataData();
		data.version = extern.version;
		data.agename = extern.agename;
		data.authors = extern.authors;
		data.datacompound = extern.datacompound;
		data.instability = extern.instability;
		data.instabilityEnabled = extern.instabilityEnabled;
		data.pages = extern.pages;
		data.seed = extern.seed;
		data.spawn = extern.spawn;
		data.symbols = extern.symbols;
		data.visited = extern.visited;
		data.worldtime = extern.worldtime;
		return data;
	}
	public static AgeDataData convert(AgeDataLoaderV4_1.AgeDataData extern) {
		AgeDataData data = new AgeDataData();
		data.version = extern.version;
		data.agename = extern.agename;
		data.authors = extern.authors;
		data.datacompound = extern.datacompound;
		data.instability = extern.instability;
		data.instabilityEnabled = extern.instabilityEnabled;
		data.pages = extern.pages;
		data.seed = extern.seed;
		data.spawn = extern.spawn;
		data.symbols = extern.symbols;
		data.visited = extern.visited;
		data.worldtime = extern.worldtime;
		return data;
	}
	public static AgeDataData convert(AgeDataLoaderV4.AgeDataData extern) {
		AgeDataData data = new AgeDataData();
		data.version = extern.version;
		data.agename = extern.agename;
		data.authors = extern.authors;
		data.datacompound = extern.datacompound;
		data.instability = extern.instability;
		data.instabilityEnabled = extern.instabilityEnabled;
		data.pages = extern.pages;
		data.seed = extern.seed;
		data.spawn = extern.spawn;
		data.symbols = extern.symbols;
		data.visited = extern.visited;
		data.worldtime = extern.worldtime;
		return data;
	}
	public static AgeDataData convert(AgeDataLegacy.AgeDataData extern) {
		AgeDataData data = new AgeDataData();
		data.version = extern.version;
		data.agename = extern.agename;
		data.authors = extern.authors;
		data.datacompound = extern.datacompound;
		data.instability = extern.instability;
		data.instabilityEnabled = extern.instabilityEnabled;
		data.pages = extern.pages;
		data.seed = extern.seed;
		data.spawn = extern.spawn;
		data.symbols = extern.symbols;
		data.visited = extern.visited;
		data.worldtime = extern.worldtime;
		return data;
	}
}
