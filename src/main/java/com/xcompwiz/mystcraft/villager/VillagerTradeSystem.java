package com.xcompwiz.mystcraft.villager;

import com.xcompwiz.mystcraft.Mystcraft;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class VillagerTradeSystem {

	public static boolean onVillagerInteraction(EntityInteractEvent event) {
		if (event.entityPlayer.worldObj.isRemote) return false;
		if (!(event.target instanceof EntityVillager)) return false;
		EntityVillager villager = (EntityVillager) event.target;
		if (villager.getProfession() != Mystcraft.archivistId) return false;
		//TODO:
		return true;
	}

}
