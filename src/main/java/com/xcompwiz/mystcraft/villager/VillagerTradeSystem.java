package com.xcompwiz.mystcraft.villager;

import java.util.concurrent.ConcurrentMap;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import com.google.common.collect.MapMaker;
import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.ModGUIs;
import com.xcompwiz.mystcraft.inventory.InventoryVillager;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

public class VillagerTradeSystem {

	private static ConcurrentMap<EntityVillager, InventoryVillager>	villagers			= new MapMaker().weakKeys().weakValues().<EntityVillager, InventoryVillager> makeMap();
	private static long												tick_accumulator	= 0;																					;

	public static boolean onVillagerInteraction(EntityInteractEvent event) {
		if (event.entityPlayer.worldObj.isRemote) return false;
		if (!(event.target instanceof EntityVillager)) return false;
		EntityVillager villager = (EntityVillager) event.target;
		if (villager.getProfession() != Mystcraft.archivistId) return false;
		
		event.entityPlayer.openGui(Mystcraft.instance, ModGUIs.VILLAGER.ordinal(), event.entityPlayer.worldObj, villager.getEntityId(), 0, 0);
		return true;
	}

	//TODO: On tick, simulate villager restock for loaded villager inventories
	public static void onTick() {
		if (++tick_accumulator % 1000 == 0) {
			for (InventoryVillager villagerinv : villagers.values()) {
				villagerinv.simulate();
			}
		}
	}

	public static InventoryVillager getVillagerInventory(EntityVillager villager) {
		if (villager.worldObj.isRemote) return new InventoryVillager(villager);
		InventoryVillager villagerinv = villagers.get(villager);
		if (villagerinv == null) {
			villagerinv = new InventoryVillager(villager);
			villagerinv.readFromNBT(villager.getEntityData().getCompoundTag("Mystcraft").getCompoundTag("Trade"));
			villagers.put(villager, villagerinv);
			//Simulate time passing on villager inventory
			villagerinv.simulate();
		}
		return villagerinv;
	}

	public static void release(InventoryVillager villagerinv) {
		if (villagerinv.isDirty()) {
			villagerinv.writeToNBT(NBTUtils.forceGetCompound(NBTUtils.forceGetCompound(villagerinv.getVillager().getEntityData(), "Mystcraft"), "Trade"));
		}
	}

	public static int getCardCost(ItemStack itemstack) {
		if (itemstack == null) return 0;
		//TODO: Base cost of a symbol on scarcity
		Integer rank = SymbolManager.getSymbolItemCardRank(Page.getSymbol(itemstack));
		if (rank == null) return 100; //TODO: How to handle cards with missing ranks in price setting? (shouldn't come up)
		return 4 * (1 + rank);
	}
}
