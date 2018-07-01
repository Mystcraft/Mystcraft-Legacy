package com.xcompwiz.mystcraft.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

import java.nio.charset.Charset;

public abstract class PacketBase<REQ extends IMessage, REPLY extends IMessage> implements IMessage, IMessageHandler<REQ, REPLY> {

	protected ItemStack readStack(ByteBuf data) {
		return new ItemStack(readTag(data));
	}

	protected void writeStack(ByteBuf data, ItemStack stack) {
		NBTTagCompound tag = new NBTTagCompound();
		stack.writeToNBT(tag);
		writeTag(data, tag);
	}

	protected NBTTagCompound readTag(ByteBuf data) {
		try {
			return CompressedStreamTools.readCompressed(new ByteBufInputStream(data));
		} catch (Exception ignored) {
			return new NBTTagCompound();
		}
	}

	protected void writeTag(ByteBuf data, NBTTagCompound tag) {
		try {
			CompressedStreamTools.writeCompressed(tag, new ByteBufOutputStream(data));
		} catch (Exception ignored) {
		}
	}

	protected String readString(ByteBuf data) {
		int length = data.readInt();
		byte[] bytes = new byte[length];
		data.readBytes(bytes);
		return new String(bytes, Charset.forName("UTF-8"));
	}

	protected void writeString(ByteBuf data, String str) {
		byte[] bytes = str.getBytes(Charset.forName("UTF-8"));
		data.writeInt(bytes.length);
		data.writeBytes(bytes);
	}

	protected BlockPos readPos(ByteBuf data) {
		return new BlockPos(data.readInt(), data.readInt(), data.readInt());
	}

	protected void writePos(ByteBuf data, BlockPos pos) {
		data.writeInt(pos.getX());
		data.writeInt(pos.getY());
		data.writeInt(pos.getZ());
	}

}
