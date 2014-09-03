package com.xcompwiz.mystcraft.world.agedata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.world.agedata.AgeDataLoaderManager.AgeDataLoader;

public class AgeDataLoaderV4_2 extends AgeDataLoader {
	public static class AgeDataData {
		public String			agename;
		public Set<String>		authors			= new HashSet<String>();
		public long				seed;
		public short			instability;
		public boolean			instabilityEnabled;
		public ChunkCoordinates	spawn;
		public List<ItemStack>	pages			= new ArrayList<ItemStack>();
		public List<String>		symbols			= new ArrayList<String>();
		public boolean			visited;
		public NBTTagCompound	datacompound;
		public long				worldtime;
		public String			version;
	}

	@Override
	public Object load(NBTTagCompound nbttagcompound) {
		AgeDataData data = new AgeDataData();
		data.version = "4.2";
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
		for (int i = 0; i < list.tagCount(); ++i) {
			data.pages.add(Page.createPage(list.getCompoundTagAt(i)));
		}

		list = nbttagcompound.getTagList("Symbols", Constants.NBT.TAG_STRING);
		for (int i = 0; i < list.tagCount(); ++i) {
			data.symbols.add(list.getStringTagAt(i));
		}

		list = nbttagcompound.getTagList("Authors", Constants.NBT.TAG_STRING);
		for (int i = 0; i < list.tagCount(); ++i) {
			data.authors.add(list.getStringTagAt(i));
		}
		return data;
	}
}
