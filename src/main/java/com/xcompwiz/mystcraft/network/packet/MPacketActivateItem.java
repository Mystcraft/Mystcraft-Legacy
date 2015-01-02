package com.xcompwiz.mystcraft.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class MPacketActivateItem extends MPacket {

	private static byte	packetId	= (byte) 137;

	@Override
	public byte getPacketType() {
		return packetId;
	}

	@Override
	public void handle(ByteBuf data, EntityPlayer player) {
		activateItem(player, data);
	}

	private static void activateItem(EntityPlayer player, ByteBuf data) {
		int slot = data.readByte();
		ItemStack itemstack = player.inventory.getStackInSlot(slot);
		if (itemstack != null && itemstack.getItem() instanceof ItemLinking) {
			((ItemLinking) itemstack.getItem()).activate(itemstack, player.worldObj, player);
			return;
		}
		LoggerUtils.warn("Invalid Slot in ActivateItem Packet");
		return;
	}

	public static FMLProxyPacket createPacket(int slot) {
		ByteBuf data = Unpooled.buffer();

		data.writeByte(packetId);
		data.writeByte(slot);

		return buildPacket(data);
	}
}
