package com.xcompwiz.mystcraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.xcompwiz.mystcraft.network.IGuiMessageHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class MPacketGuiMessage extends PacketBase {

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		if (player.openContainer == null) return;
		int windowId = data.readInt();
		if (player.openContainer.windowId != windowId) return;
		if (!player.openContainer.isPlayerNotUsingContainer(player)) return;
		if (player.openContainer instanceof IGuiMessageHandler) {
			NBTTagCompound nbt = ByteBufUtils.readTag(data);
			((IGuiMessageHandler) player.openContainer).processMessage(player, nbt);
		}
	}

	public static FMLProxyPacket createPacket(int windowId, NBTTagCompound nbttagcompound) {
		ByteBuf data = PacketBase.createDataBuffer((Class<? extends PacketBase>) new Object() {}.getClass().getEnclosingClass());

		data.writeInt(windowId);
		ByteBufUtils.writeTag(data, nbttagcompound);

		return buildPacket(data);
	}
}
