package com.xcompwiz.mystcraft.inventory;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.treasure.WeightProviderSymbolItem;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector;
import com.xcompwiz.mystcraft.villager.VillagerTradeSystem;

public class InventoryVillager {
	private static final long	step_size	= 12000;

	private boolean			dirty;
	private long			lastrestock;
	private long			lastupdated;

	private EntityVillager	villager;
	private Random			rand			= new Random();

	private ItemStack		pageitems[]		= new ItemStack[3];
	private int				boostercount	= 5;

	public InventoryVillager(EntityVillager villager) {
		this.villager = villager;
	}

	public Entity getVillager() {
		return villager;
	}

	public void markDirty() {
		this.setDirty(true);
	}

	public void setDirty(boolean b) {
		dirty = b;
	}

	public boolean isDirty() {
		return dirty;
	}

	public long getLastChanged() {
		return lastupdated;
	}

	private void markUpdated() {
		lastupdated = villager.worldObj.getTotalWorldTime();
		markDirty();
	}

	public int getBoosterCount() {
		return boostercount;
	}

	public int getBoosterCost() {
		return 20;
	}

	public void simulate() {
		long deltatime = villager.worldObj.getTotalWorldTime() - lastrestock;
		while (deltatime >= 100) {
			deltatime -= step_size;
			restock();
		}
	}

	private void restock() {
		lastrestock = villager.worldObj.getTotalWorldTime();
		int nonechance = 1;
		int boosterchance = 3;
		int roll = rand.nextInt(nonechance + boosterchance + pageitems.length);
		roll -= nonechance;
		if (roll < 0) {
			return;
		}
		roll -= boosterchance;
		if (roll < 0) {
			if (boostercount < 8) {
				++boostercount;
				markUpdated();
			}
			return;
		}
		if (pageitems[roll] != null && pageitems[roll].stackSize < 5) ++pageitems[roll].stackSize;
		markUpdated();
	}

	public boolean purchaseBooster(InventoryPlayer inventoryplayer) {
		if (boostercount <= 0) return false;
		int playerEmeralds = getPlayerEmeralds(inventoryplayer);
		int price = getBoosterCost();
		ItemStack booster = new ItemStack(ModItems.booster);
		if (playerEmeralds < price) return false;
		//Add booster to inventory
		if (!inventoryplayer.addItemStackToInventory(booster)) return false; //If fail, abort
		//Remove emeralds from user inventory
		if (!deductPrice(inventoryplayer, price)) {
			InventoryUtils.removeFromInventory(inventoryplayer, booster, 1);
			return false;
		}
		//Decrease available boosters
		--boostercount;
		markUpdated();
		return true;
	}

	public boolean purchaseShopItem(InventoryPlayer inventoryplayer, int index) {
		ItemStack original = getShopItem(index);
		if (original == null) return false;
		ItemStack clone = original.copy();
		if (clone.stackSize <= 0) return false;
		clone.stackSize = 1;
		int price = getShopItemPrice(index);
		int playerEmeralds = getPlayerEmeralds(inventoryplayer);
		if (playerEmeralds < price) return false;
		//Add booster to inventory
		if (!inventoryplayer.addItemStackToInventory(clone)) return false; //If fail, abort
		//Remove emeralds from user inventory
		if (!deductPrice(inventoryplayer, price)) {
			InventoryUtils.removeFromInventory(inventoryplayer, clone, 1);
			return false;
		}
		//Decrease available boosters
		--original.stackSize;
		markUpdated();
		return true;
	}

	private boolean deductPrice(InventoryPlayer inventoryplayer, int price) {
		int playerchange = InventoryUtils.countInInventory(inventoryplayer, new ItemStack(Items.emerald));
		if (playerchange < price % 9) {
			if (!inventoryplayer.addItemStackToInventory(new ItemStack(Items.emerald, 9))) return false;
			InventoryUtils.removeFromInventory(inventoryplayer, new ItemStack(Blocks.emerald_block), 1);
		}
		price = price % 9 + 9 * InventoryUtils.removeFromInventory(inventoryplayer, new ItemStack(Blocks.emerald_block), price / 9);
		price = InventoryUtils.removeFromInventory(inventoryplayer, new ItemStack(Items.emerald), price);
		return true;
	}

	public Integer getPlayerEmeralds(InventoryPlayer inventoryplayer) {
		return InventoryUtils.countInInventory(inventoryplayer, new ItemStack(Items.emerald)) + 9 * InventoryUtils.countInInventory(inventoryplayer, new ItemStack(Blocks.emerald_block));
	}

	public ItemStack getShopItem(int index) {
		if (this.pageitems[index] == null && !villager.worldObj.isRemote) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(new Random(), SymbolManager.getSymbolsByRank(index + 1, null), WeightProviderSymbolItem.instance);
			this.pageitems[index] = Page.createSymbolPage(symbol.identifier());
			this.pageitems[index].stackSize = 3;
		}
		return this.pageitems[index];
	}

	public int getShopItemPrice(int index) {
		ItemStack itemstack = this.getShopItem(index);
		return VillagerTradeSystem.getCardCost(itemstack);
	}

	public void writeToNBT(NBTTagCompound data) {
		data.setTag("Inventory", NBTUtils.writeInventoryArray(new NBTTagList(), this.pageitems));
		data.setInteger("boostercount", boostercount);
		data.setLong("lastrestock", lastrestock);
	}

	public void readFromNBT(NBTTagCompound data) {
		NBTUtils.readInventoryArray(data.getTagList("Inventory", Constants.NBT.TAG_COMPOUND), this.pageitems);
		if (data.hasKey("boostercount")) boostercount = data.getInteger("boostercount");
		if (data.hasKey("lastrestock")) lastrestock = data.getInteger("lastrestock");

		// Initial generation
		for (int i = 0; i < pageitems.length; ++i)
			getShopItem(i);
		if (!this.villager.worldObj.isRemote) markUpdated();
	}
}
