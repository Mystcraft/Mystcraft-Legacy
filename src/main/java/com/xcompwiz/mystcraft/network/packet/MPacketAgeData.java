package com.xcompwiz.mystcraft.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.world.agedata.AgeData;

import cpw.mods.fml.common.network.ByteBufUtils;

public class MPacketAgeData extends PacketHandler {

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		readDataPacket(player.worldObj, data);
	}

	private static void readDataPacket(World worldObj, ByteBuf data) {
		int uId = data.readInt();
		NBTTagCompound nbt = ByteBufUtils.readTag(data);
		AgeData agedata = AgeData.getMPAgeData(uId);
		agedata.readFromNBT(nbt);
		return;
	}

	public static Packet getDataPacket(int uid) {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		AgeData.getAge(uid, false).writeToNBT(nbttagcompound);

		ByteBuf data = PacketHandler.createDataBuffer(MPacketAgeData.class);

		data.writeInt(uid);
		ByteBufUtils.writeTag(data, nbttagcompound);

		Packet pkt = buildPacket(data);
		return pkt;
	}

}
