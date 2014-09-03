package com.xcompwiz.mystcraft.world.agedata;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.world.agedata.AgeData.AgeDataData;

public class AgeDataLoaderManager {

	public static abstract class AgeDataLoader {
		public abstract AgeDataData load(NBTTagCompound nbt);
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
		AgeDataData data = loader.load(nbt);
		update(data);
		return data;
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
			AgeDataLoaderV4_1.AgeDataData datav = (com.xcompwiz.mystcraft.world.agedata.AgeDataLoaderV4_1.AgeDataData) data;
			if (datav.effects.size() > 0) data.cruft.put("instabilityeffects", NBTUtils.writeStringListToNBT(new NBTTagList(), datav.effects));
			data.version = "4.2";
		}

		if (data.agename.isEmpty()) {
			data.agename = "Unnamed Age";
		}
		if (!data.version.equals(currentversion)) {
			throw new RuntimeException("Error updating old agedata file! Version is " + data.version);
		}
	}
}
