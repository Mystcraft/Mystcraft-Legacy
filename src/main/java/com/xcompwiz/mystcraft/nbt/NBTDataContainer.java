package com.xcompwiz.mystcraft.nbt;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public class NBTDataContainer extends WorldSavedData {

	public NBTTagCompound data = new NBTTagCompound();

	public NBTDataContainer(String id) {
		super(id);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtstore) {
		data = nbtstore.getCompoundTag("Data");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtstore) {
		nbtstore.setTag("Data", data);
		return nbtstore;
	}

}
