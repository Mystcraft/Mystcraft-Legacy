package com.xcompwiz.mystcraft.network.packet;

import com.xcompwiz.mystcraft.world.agedata.AgeData;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class MPacketAgeData extends PacketBase {

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		readDataPacket(player.world, data);
	}

	private static void readDataPacket(World worldObj, ByteBuf data) {
		int uId = data.readInt();
		NBTTagCompound nbt = ByteBufUtils.readTag(data);
		AgeData agedata = AgeData.getMPAgeData(uId);
		agedata.readFromNBT(nbt);
		return;
	}

	public static FMLProxyPacket getDataPacket(int uid) {
		NBTTagCompound nbttagcompound = AgeData.getAge(uid, false).writeToNBT(new NBTTagCompound());

		ByteBuf data = PacketBase.createDataBuffer((Class<? extends PacketBase>) new Object() {}.getClass().getEnclosingClass());

		data.writeInt(uid);
		ByteBufUtils.writeTag(data, nbttagcompound);

		return buildPacket(data);
	}

}
