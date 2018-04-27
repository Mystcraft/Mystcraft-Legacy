package com.xcompwiz.mystcraft.treasure;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class LootTableHandler {

    public static final LootTableHandler EVENT_INSTANCE = new LootTableHandler();

    public static final ResourceLocation MYST_TREASURE = new ResourceLocation(MystObjects.MystcraftModId, "mystcraft_treasure");

    private static final ImmutableSet<ResourceLocation> pageTables = ImmutableSet.of(
            LootTableList.CHESTS_DESERT_PYRAMID,
            LootTableList.CHESTS_JUNGLE_TEMPLE,
            LootTableList.CHESTS_STRONGHOLD_LIBRARY,
            LootTableList.CHESTS_SIMPLE_DUNGEON);

    public static void init() {
        LootTableList.register(MYST_TREASURE);
    }

    @SubscribeEvent
    public void lootLoad(LootTableLoadEvent event) {
        LootTable lt = event.getTable();
        if(event.getName().equals(MYST_TREASURE)) {
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
        } else if(pageTables.contains(event.getName())) {
            LootPool mainPool = lt.getPool("main");
            mainPool.addEntry(new TreasureGenWrapper(10, 0, "myst_treasure_hook"));
        }
    }

}
