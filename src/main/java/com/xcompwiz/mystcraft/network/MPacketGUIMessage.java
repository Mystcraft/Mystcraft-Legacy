package com.xcompwiz.mystcraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class MPacketGuiMessage extends MPacket {

	private static byte	packetId	= (byte) 140;

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

	@Override
	public byte getPacketType() {
		return packetId;
	}

	public static FMLProxyPacket createPacket(int windowId, NBTTagCompound nbttagcompound) {
		ByteBuf data = Unpooled.buffer();

		data.writeByte(packetId);
		data.writeInt(windowId);
		ByteBufUtils.writeTag(data, nbttagcompound);

		return buildPacket(data);
	}
}
