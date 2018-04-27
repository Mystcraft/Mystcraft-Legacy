package com.xcompwiz.mystcraft.inventory;

import java.util.Arrays;
import java.util.Random;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.treasure.WeightProviderSymbolItem;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector;
import com.xcompwiz.mystcraft.villager.VillagerTradeSystem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

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
		Arrays.fill(pageitems, ItemStack.EMPTY);
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
		lastupdated = villager.world.getTotalWorldTime();
		markDirty();
	}

	public int getBoosterCount() {
		return boostercount;
	}

	public int getBoosterCost() {
		return 20;
	}

	public void simulate() {
		long deltatime = villager.world.getTotalWorldTime() - lastrestock;
		while (deltatime >= step_size) {
			deltatime -= step_size;
			lastrestock += step_size;
			restock();
		}
	}

	private void restock() {
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
		if (!pageitems[roll].isEmpty() && pageitems[roll].getCount() < 5) {
			pageitems[roll].grow(1);
		}
		markUpdated();
	}

	//Use the handler for querying things, the inventoryPlayer for ease of use for certain methods... *shrugs*
	public boolean purchaseBooster(IItemHandlerModifiable inventoryHandlerPlayer, InventoryPlayer inventoryHelperPlayer) {
		if (boostercount <= 0) return false;
		int playerEmeralds = getPlayerEmeralds(inventoryHandlerPlayer);
		int price = getBoosterCost();
		ItemStack booster = new ItemStack(ModItems.booster);
		if (playerEmeralds < price) return false;
		//Add booster to inventory
		if (!inventoryHelperPlayer.addItemStackToInventory(booster)) return false; //If fail, abort
		//Remove emeralds from user inventory
		if (!deductPrice(inventoryHandlerPlayer, inventoryHelperPlayer, price)) {
			InventoryUtils.removeFromInventory(inventoryHandlerPlayer, booster, 1);
			return false;
		}
		//Decrease available boosters
		--boostercount;
		markUpdated();
		return true;
	}

	public boolean purchaseShopItem(IItemHandlerModifiable inventoryHandlerPlayer, InventoryPlayer inventoryHelperPlayer, int index) {
		ItemStack original = getShopItem(index);
		if (original.isEmpty()) return false;
		ItemStack clone = original.copy();
		if (clone.getCount() <= 0) return false;
		clone.setCount(1);
		int price = getShopItemPrice(index);
		int playerEmeralds = getPlayerEmeralds(inventoryHandlerPlayer);
		if (playerEmeralds < price) return false;
		//Add booster to inventory
		if (!inventoryHelperPlayer.addItemStackToInventory(clone)) return false; //If fail, abort
		//Remove emeralds from user inventory
		if (!deductPrice(inventoryHandlerPlayer, inventoryHelperPlayer, price)) {
			InventoryUtils.removeFromInventory(inventoryHandlerPlayer, clone, 1);
			return false;
		}
		//Decrease available boosters
		original.shrink(1);
		markUpdated();
		return true;
	}

	private boolean deductPrice(IItemHandlerModifiable handlerPlayer, InventoryPlayer inventoryplayer, int price) {
		int playerchange = InventoryUtils.countInInventory(handlerPlayer, new ItemStack(Items.EMERALD));
		if (playerchange < price % 9) {
			if (!inventoryplayer.addItemStackToInventory(new ItemStack(Items.EMERALD, 9))) return false;
			InventoryUtils.removeFromInventory(handlerPlayer, new ItemStack(Blocks.EMERALD_BLOCK), 1);
		}
		price = price % 9 + 9 * InventoryUtils.removeFromInventory(handlerPlayer, new ItemStack(Blocks.EMERALD_BLOCK), price / 9);
		price = InventoryUtils.removeFromInventory(handlerPlayer, new ItemStack(Items.EMERALD), price);
		return true;
	}

	public Integer getPlayerEmeralds(IItemHandlerModifiable inventoryplayer) {
		return InventoryUtils.countInInventory(inventoryplayer, new ItemStack(Items.EMERALD)) + 9 * InventoryUtils.countInInventory(inventoryplayer, new ItemStack(Blocks.EMERALD_BLOCK));
	}

	@Nonnull
	public ItemStack getShopItem(int index) {
		if (this.pageitems[index].isEmpty() && !villager.world.isRemote) {
			IAgeSymbol symbol = WeightedItemSelector.getRandomItem(new Random(), SymbolManager.getSymbolsByRank(index + 1, null), WeightProviderSymbolItem.instance);
			this.pageitems[index] = Page.createSymbolPage(symbol.getRegistryName());
			this.pageitems[index].setCount(3);
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
		for (int i = 0; i < pageitems.length; ++i) {
			getShopItem(i);
		}
		if (!this.villager.world.isRemote) {
			markUpdated();
		}
	}
}
