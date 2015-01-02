package com.xcompwiz.mystcraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import com.xcompwiz.mystcraft.client.gui.GuiHandlerManager;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class MPacketOpenWindow extends PacketHandler {

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		byte guitype = data.readByte();
		byte windowId = data.readByte();
		GuiHandlerManager.displayGui(player, guitype, data);
		player.openContainer.windowId = windowId;
	}

	public static FMLProxyPacket createPacket(int windowId, int guiId, TileEntity tileEntity) {
		ByteBuf data = PacketHandler.createDataBuffer(MPacketOpenWindow.class);

		data.writeByte(guiId);
		data.writeByte(windowId);
		data.writeInt(tileEntity.xCoord);
		data.writeInt(tileEntity.yCoord);
		data.writeInt(tileEntity.zCoord);

		return buildPacket(data);
	}

	public static FMLProxyPacket createPacket(int windowId, int guiId, Entity entity) {
		ByteBuf data = PacketHandler.createDataBuffer(MPacketOpenWindow.class);

		data.writeByte(guiId);
		data.writeByte(windowId);
		data.writeInt(entity.getEntityId());

		return buildPacket(data);
	}

	public static FMLProxyPacket createPacket(int windowId, int guiId, byte slot) {
		ByteBuf data = PacketHandler.createDataBuffer(MPacketOpenWindow.class);

		data.writeByte(guiId);
		data.writeByte(windowId);
		data.writeByte(slot);

		return buildPacket(data);
	}

}
