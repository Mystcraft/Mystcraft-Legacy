package com.xcompwiz.mystcraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import com.xcompwiz.mystcraft.client.gui.GuiHandlerManager;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class MPacketOpenWindow extends PacketBase {

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		byte guitype = data.readByte();
		byte windowId = data.readByte();
		GuiHandlerManager.displayGui(player, guitype, data);
		player.openContainer.windowId = windowId;
	}

	public static FMLProxyPacket createPacket(int windowId, int guiId, TileEntity tileEntity) {
		ByteBuf data = PacketBase.createDataBuffer((Class<? extends PacketBase>) new Object() {}.getClass().getEnclosingClass());

		data.writeByte(guiId);
		data.writeByte(windowId);
		data.writeInt(tileEntity.xCoord);
		data.writeInt(tileEntity.yCoord);
		data.writeInt(tileEntity.zCoord);

		return buildPacket(data);
	}

	public static FMLProxyPacket createPacket(int windowId, int guiId, Entity entity) {
		ByteBuf data = PacketBase.createDataBuffer((Class<? extends PacketBase>) new Object() {}.getClass().getEnclosingClass());

		data.writeByte(guiId);
		data.writeByte(windowId);
		data.writeInt(entity.getEntityId());

		return buildPacket(data);
	}

	public static FMLProxyPacket createPacket(int windowId, int guiId, byte slot) {
		ByteBuf data = PacketBase.createDataBuffer((Class<? extends PacketBase>) new Object() {}.getClass().getEnclosingClass());

		data.writeByte(guiId);
		data.writeByte(windowId);
		data.writeByte(slot);

		return buildPacket(data);
	}

}
