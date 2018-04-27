package com.xcompwiz.mystcraft.world.agedata;

import java.util.HashMap;
import java.util.UUID;

import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.world.agedata.AgeData.AgeDataData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AgeDataLoaderManager {

	public static abstract class AgeDataLoader {
		public abstract AgeDataData load(NBTTagCompound nbt);
	}

	private static final String						currentversion	= "4.3"; //XXX: Current agedata version stored in multiple locations

	private static HashMap<String, AgeDataLoader>	loaders			= new HashMap<String, AgeDataLoader>();

	static {
		//XXX: Current agedata version stored in multiple locations
		loaders.put("4.3", new AgeDataLoaderV4_3());
		//loaders.put("4.2", new AgeDataLoaderV4_2());
		//loaders.put("4.1", new AgeDataLoaderV4_1());
		//loaders.put("4.0", new AgeDataLoaderV4());
		//loaders.put("", new AgeDataLegacy());
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
		//if (data.version.equals("legacy")) {
		//	data.version = "4.0";
		//}
		//// Update 4.0 -> 4.1
		//if (data.version.equals("4.0")) {
		//	data.pages.add(0, Page.createLinkPage());
		//	data.version = "4.1";
		//}
//
		//// Update 4.1 -> 4.2
		//if (data.version.equals("4.1")) {
		//	AgeDataLoaderV4_1.AgeDataData datav = (com.xcompwiz.mystcraft.world.agedata.AgeDataLoaderV4_1.AgeDataData) data;
		//	if (datav.effects.size() > 0) data.cruft.put("instabilityeffects", NBTUtils.writeStringCollection(new NBTTagList(), datav.effects));
		//	data.version = "4.2";
		//}
//
		//// Update 4.2 -> 4.3
		//if (data.version.equals("4.2")) {
		//	data.uuid = UUID.randomUUID();
		//	data.dead = false;
		//	data.version = "4.3";
		//}
//
		//// Sanity fix
		//if (data.agename == null || data.agename.isEmpty()) {
		//	data.agename = "Unnamed Age";
		//}
		//if (!data.version.equals(currentversion)) { throw new RuntimeException("Error updating old agedata file! Version is " + data.version); }
	}
}
