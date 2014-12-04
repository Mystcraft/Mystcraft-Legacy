package com.xcompwiz.mystcraft.inventory;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.xcompwiz.mystcraft.network.IGuiMessageHandler;

public class ContainerVillagerShop extends ContainerBase implements IGuiMessageHandler {

	private EntityVillager	villager;

	public ContainerVillagerShop(InventoryPlayer inventory, EntityVillager villager) {
		this.villager = villager;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		if (villager.isDead) return false;
		return entityplayer.getDistanceSq(villager.posX, villager.posY, villager.posZ) <= 64D;
	}

	@Override
	public void processMessage(EntityPlayer player, NBTTagCompound nbttagcompound) {}

}
