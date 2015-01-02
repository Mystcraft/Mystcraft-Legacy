package com.xcompwiz.mystcraft.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.network.IMessageReceiver;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class MPacketMessage extends MPacket {

	private static byte	packetId	= (byte) 132;

	@Override
	public byte getPacketType() {
		return packetId;
	}

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		sendMessage(player, data);
	}

	private static void sendMessage(EntityPlayer player, ByteBuf data) {
		NBTTagCompound nbt = ByteBufUtils.readTag(data);
		if (nbt.hasKey("ID")) {
			Entity entity = Mystcraft.sidedProxy.getEntityByID(player.worldObj, nbt.getInteger("ID"));
			if (entity instanceof IMessageReceiver) {
				IMessageReceiver receiver = (IMessageReceiver) entity;
				receiver.processMessageData(nbt.getCompoundTag("Data"));
				return;
			} else if (entity != null) {
				entity.readFromNBT(nbt.getCompoundTag("Data"));
				return;
			}
		} else if (nbt.hasKey("X") && nbt.hasKey("Y") && nbt.hasKey("Z")) {
			if (player.worldObj.blockExists(nbt.getInteger("X"), nbt.getInteger("Y"), nbt.getInteger("Z"))) {
				TileEntity tileentity = player.worldObj.getTileEntity(nbt.getInteger("X"), nbt.getInteger("Y"), nbt.getInteger("Z"));
				if (tileentity instanceof IMessageReceiver) {
					IMessageReceiver receiver = (IMessageReceiver) tileentity;
					receiver.processMessageData(nbt.getCompoundTag("Data"));
					return;
				} else if (tileentity != null) {
					tileentity.readFromNBT(nbt.getCompoundTag("Data"));
					return;
				}
			}
		}
	}

	public static FMLProxyPacket createPacket(TileEntity tile, NBTTagCompound nbttagcompound) {
		ByteBuf data = Unpooled.buffer();
		data.writeByte(packetId);

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Data", nbttagcompound);
		nbt.setInteger("X", tile.xCoord);
		nbt.setInteger("Y", tile.yCoord);
		nbt.setInteger("Z", tile.zCoord);

		ByteBufUtils.writeTag(data, nbt);

		return buildPacket(data);
	}

	public static FMLProxyPacket createPacket(Entity entity, NBTTagCompound nbttagcompound) {
		ByteBuf data = Unpooled.buffer();

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Data", nbttagcompound);
		nbt.setInteger("ID", entity.getEntityId());

		data.writeByte(packetId);
		ByteBufUtils.writeTag(data, nbt);

		return buildPacket(data);
	}
}
