package com.xcompwiz.mystcraft.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IGuiMessageHandler {

	public abstract void processMessage(EntityPlayer player, NBTTagCompound nbttagcompound);
}
