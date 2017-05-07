package com.xcompwiz.mystcraft.network.packet;

import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

//TODO: This packet is antiquated
public class MPacketActivateItem extends PacketBase {

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
		ByteBuf data = PacketBase.createDataBuffer((Class<? extends PacketBase>) new Object() {}.getClass().getEnclosingClass());

		data.writeByte(slot);

		return buildPacket(data);
	}
}
