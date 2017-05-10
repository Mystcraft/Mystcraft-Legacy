package com.xcompwiz.mystcraft.treasure;

import java.util.Collection;
import java.util.Random;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class TreasureGenWrapper extends LootEntry {

	//Hellfire> the 'name' can be many things. in vanilla it describes for example the item-registryName of the loot-roll
	public TreasureGenWrapper(int weight, int quality, String name) {
		this(weight, quality, name, new LootCondition[0]);
	}

	public TreasureGenWrapper(int weight, int quality, String name, LootCondition[] lootConditions) {
		super(weight, quality, lootConditions, name);
	}

	@Override
	//Called whenever this loottable is supposed to roll itemstacks and the given lootconditions apply to the context.
	//The LootContext holds much information on what this lootroll is about.
	public void addLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {

	}

	//Hellfire> We don't *really* need to do this. This is actually never really used from what i gather
	@Override
	protected void serialize(JsonObject json, JsonSerializationContext context) {}

}
