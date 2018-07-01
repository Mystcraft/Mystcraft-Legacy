package com.xcompwiz.mystcraft.world.agedata;

import java.util.UUID;

import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.world.agedata.AgeDataLoaderManager.AgeDataLoader;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

public class AgeDataLoaderV4_3 extends AgeDataLoader {

	public static class AgeDataData extends AgeData.AgeDataData {}

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
			data.spawn = new BlockPos(nbttagcompound.getInteger("SpawnX"), nbttagcompound.getInteger("SpawnY"), nbttagcompound.getInteger("SpawnZ"));
		}

		NBTTagList list;
		list = nbttagcompound.getTagList("Pages", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); ++i) {
			data.pages.add(Page.createPage(list.getCompoundTagAt(i)));
		}

		nbttagcompound.getTagList("Symbols", Constants.NBT.TAG_STRING).forEach(nbtbase -> {
			if (nbtbase instanceof NBTTagString) {
				data.symbols.add(new ResourceLocation(((NBTTagString) nbtbase).getString()));
			} else {
				data.symbols.add(new ResourceLocation(nbtbase.toString()));
			}
		});
		NBTUtils.readStringCollection(nbttagcompound.getTagList("Authors", Constants.NBT.TAG_STRING), data.authors);

		NBTTagCompound cruftnbt = nbttagcompound.getCompoundTag("Cruft");
		for (String key : cruftnbt.getKeySet()) {
			data.cruft.put(key, cruftnbt.getTag(key));
		}
		return data;
	}
}
