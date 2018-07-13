package com.xcompwiz.mystcraft.network;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IGuiMessageHandler {

	//Receives its messages from MPacketGuiMessage.onMessage
	void processMessage(@Nonnull EntityPlayer player, @Nonnull NBTTagCompound nbttagcompound);

}
