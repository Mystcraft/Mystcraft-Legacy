package com.xcompwiz.mystcraft.network;

import net.minecraft.nbt.NBTTagCompound;

public interface IMessageReceiver {

	public abstract void processMessageData(NBTTagCompound nbttagcompound);
}
