package com.xcompwiz.mystcraft.world.agedata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.world.agedata.AgeDataLoaderManager.AgeDataLoader;

public class AgeDataLoaderV4 extends AgeDataLoader {
	public static class AgeDataData extends com.xcompwiz.mystcraft.world.agedata.AgeDataLoaderV4_1.AgeDataData {}

	@Override
	public AgeDataData load(NBTTagCompound nbttagcompound) {
		AgeDataData data = new AgeDataData();
		data.version = "4.0";
		data.agename = nbttagcompound.getString("AgeName");
		data.seed = nbttagcompound.getLong("Seed");
		data.visited = nbttagcompound.getBoolean("Visited");
		data.worldtime = nbttagcompound.getLong("WorldTime");

		data.instability = nbttagcompound.getShort("BaseIns");
		data.instabilityEnabled = nbttagcompound.getBoolean("InstabilityEnabled");

		data.datacompound = nbttagcompound.getCompoundTag("DataCompound");

		if (nbttagcompound.hasKey("SpawnX") && nbttagcompound.hasKey("SpawnY") && nbttagcompound.hasKey("SpawnZ")) {
			data.spawn = new ChunkCoordinates(nbttagcompound.getInteger("SpawnX"), nbttagcompound.getInteger("SpawnY"), nbttagcompound.getInteger("SpawnZ"));
		}

		NBTTagList list = null;
		list = nbttagcompound.getTagList("Pages", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			data.pages.add(Page.createPage(list.getCompoundTagAt(i)));
		}

		NBTUtils.readStringCollection(nbttagcompound.getTagList("Symbols", Constants.NBT.TAG_STRING), data.symbols);
		NBTUtils.readStringCollection(nbttagcompound.getTagList("Effects", Constants.NBT.TAG_STRING), data.effects);
		NBTUtils.readStringCollection(nbttagcompound.getTagList("Authors", Constants.NBT.TAG_STRING), data.authors);

		return data;
	}
}
