package com.xcompwiz.mystcraft.network.packet;

import com.xcompwiz.mystcraft.world.agedata.AgeData;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MPacketAgeData extends PacketBase<MPacketAgeData, MPacketAgeData> {

	private NBTTagCompound tag;
	private int id;

	public MPacketAgeData() {}

	public MPacketAgeData(int id) {
		this.id = id;
		this.tag = AgeData.getAge(id, false).writeToNBT(new NBTTagCompound());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
		this.tag = readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
		writeTag(buf, tag);
	}

	@Override
	public MPacketAgeData onMessage(MPacketAgeData message, MessageContext ctx) {
		readData(message);
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void readData(MPacketAgeData message) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			AgeData agedata = AgeData.getMPAgeData(message.id);
			agedata.readFromNBT(message.tag);
		});
	}

}
