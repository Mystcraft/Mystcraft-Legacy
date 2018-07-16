package com.xcompwiz.mystcraft.treasure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.tileentity.InventoryFilter;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LootTableHandler {

	public static final LootTableHandler EVENT_INSTANCE = new LootTableHandler();

	public static final ResourceLocation MYST_TREASURE = new ResourceLocation(MystObjects.MystcraftModId, "mystcraft_treasure");

	private static final ImmutableSet<ResourceLocation> pageTables = ImmutableSet.of(LootTableList.CHESTS_DESERT_PYRAMID, LootTableList.CHESTS_JUNGLE_TEMPLE, LootTableList.CHESTS_STRONGHOLD_LIBRARY, LootTableList.CHESTS_SIMPLE_DUNGEON);

	public static void init() {
		LootTableList.register(MYST_TREASURE);
	}

	@SubscribeEvent
	public void lootLoad(LootTableLoadEvent event) {
		LootTable lt = event.getTable();
		if (event.getName().equals(MYST_TREASURE)) {
			LootPool main = lt.getPool(MYST_TREASURE.toString());
			ArrayList<IAgeSymbol> symbols = SymbolManager.getAgeSymbols();
			for (IAgeSymbol symbol : symbols) {
				int maxStack = SymbolManager.getSymbolTreasureMaxStack(symbol);
				int chance = SymbolManager.getSymbolItemWeight(symbol.getRegistryName());
				ItemStack symbolStack = Page.createSymbolPage(symbol.getRegistryName());
				main.addEntry(new LootEntry(chance, 0, new LootCondition[0], symbol.getRegistryName().toString()) {
					@Override
					public void addLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {
						ItemStack copy = symbolStack.copy();
						copy.setCount(1 + rand.nextInt(Math.max(1, maxStack - 1)));
						stacks.add(copy);
					}

					@Override
					protected void serialize(JsonObject json, JsonSerializationContext context) {}
				});
			}
		} else if (pageTables.contains(event.getName())) {
			LootPool mainPool = lt.getPool("main");
			mainPool.addEntry(new TreasureGenWrapper(10, 0, "myst_treasure_hook"));
		}
	}

	public static ItemStack generateLecternItem(InventoryFilter filter, Random rand, LootTable lootTable, LootContext lootContext) {
		int failsafe = 0;
		ItemStack item = null;
		while (item == null && failsafe++ < 100) {
			List<ItemStack> result = lootTable.generateLootForPools(rand, lootContext);

			for (ItemStack stack : result) {
				if (stack.getCount() > 0)
					stack.setCount(1);

				if (filter != null && !filter.canAcceptItem(0, stack))
					continue;

				if (stack.getItem() == ModItems.page) {
					ResourceLocation symbolid = Page.getSymbol(stack);
					if (symbolid == null)
						continue;

					Integer symbolRank = SymbolManager.getSymbolItemCardRank(symbolid);
					if (symbolRank == null || symbolRank < 3)
						continue;
				}

				return stack;
			}
		}
		return null;
	}

}
