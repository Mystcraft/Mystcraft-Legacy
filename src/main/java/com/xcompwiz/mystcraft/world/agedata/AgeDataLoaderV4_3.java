package com.xcompwiz.mystcraft.world.agedata;

import java.util.Set;
import java.util.UUID;

import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.world.agedata.AgeDataLoaderManager.AgeDataLoader;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.Constants;

public class AgeDataLoaderV4_3 extends AgeDataLoader {
	public static class AgeDataData extends com.xcompwiz.mystcraft.world.agedata.AgeData.AgeDataData {}

	@Override
	public AgeDataData load(NBTTagCompound nbttagcompound) {
		AgeDataData data = new AgeDataData();
		data.version = "4.3";
		data.agename = nbttagcompound.getString("AgeName");
		data.seed = nbttagcompound.getLong("Seed");
		data.uuid = UUID.fromString(nbttagcompound.getString("UUID"));
		data.visited = nbttagcompound.getBoolean("Visited");
		data.dead = nbttagcompound.getBoolean("Dead");
		data.worldtime = nbttagcompound.getLong("WorldTime");

		data.instability = nbttagcompound.getShort("BaseIns");
		data.instabilityEnabled = nbttagcompound.getBoolean("InstabilityEnabled");

		data.datacompound = nbttagcompound.getCompoundTag("DataCompound");

		if (nbttagcompound.hasKey("SpawnX") && nbttagcompound.hasKey("SpawnY") && nbttagcompound.hasKey("SpawnZ")) {
			data.spawn = new ChunkCoordinates(nbttagcompound.getInteger("SpawnX"), nbttagcompound.getInteger("SpawnY"), nbttagcompound.getInteger("SpawnZ"));
		}

		NBTTagList list = null;
		list = nbttagcompound.getTagList("Pages", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); ++i) {
			data.pages.add(Page.createPage(list.getCompoundTagAt(i)));
		}

		NBTUtils.readStringCollection(nbttagcompound.getTagList("Symbols", Constants.NBT.TAG_STRING), data.symbols);
		NBTUtils.readStringCollection(nbttagcompound.getTagList("Authors", Constants.NBT.TAG_STRING), data.authors);

		NBTTagCompound cruftnbt = nbttagcompound.getCompoundTag("Cruft");
		for (String key : (Set<String>) cruftnbt.func_150296_c()) {
			data.cruft.put(key, cruftnbt.getTag(key));
		}
		return data;
	}
}
