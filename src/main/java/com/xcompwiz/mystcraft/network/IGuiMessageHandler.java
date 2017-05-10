package com.xcompwiz.mystcraft.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public interface IGuiMessageHandler {

	//Receives its messages from MPacketGuiMessage.onMessage
	void processMessage(@Nonnull EntityPlayer player, @Nonnull NBTTagCompound nbttagcompound);

}
