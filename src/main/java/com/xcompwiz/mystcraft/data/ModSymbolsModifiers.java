package com.xcompwiz.mystcraft.data;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.symbol.ISymbolFactory.CategoryPair;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierBlock;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierColor;
import com.xcompwiz.util.CollectionUtils;

import cpw.mods.fml.common.event.FMLInterModComms;

public class ModSymbolsModifiers {

	public static class BlockModifierContainerObject {
		private BlockDescriptor	descriptor;
		private ModifierBlock	symbol;

		private BlockModifierContainerObject(BlockDescriptor descriptor, ModifierBlock symbol) {
			this.descriptor = descriptor;
			this.symbol = symbol;
		}

		private BlockModifierContainerObject() {}

		public BlockModifierContainerObject add(BlockCategory cat, Integer rank) {
			if (descriptor == null || symbol == null) return this;
			if (!descriptor.isUsable(cat)) {
				descriptor.setUsable(cat, true);
				this.symbol.addRule(new Rule(cat.getGrammarBinding(), CollectionUtils.buildList(symbol.identifier()), rank));
			}
			return this;
		}

		public static BlockModifierContainerObject create(String word, int cardrank, Block block, byte metadata) {
			BlockDescriptor descriptor = new BlockDescriptor(block, metadata);
			ModifierBlock symbol = new ModifierBlock(descriptor, word);
			if (SymbolManager.hasBinding(symbol.identifier())) {
				LoggerUtils.info("Some Mod is attempting to register a block symbol over an existing registration.");
				return new BlockModifierContainerObject();
			}
			symbol.setCardRank(cardrank);
			return new BlockModifierContainerObject(descriptor, symbol);
		}

		public static BlockModifierContainerObject create(String word, int cardrank, Block block, int metadata) {
			return BlockModifierContainerObject.create(word, cardrank, block, (byte) metadata);
		}

		public IAgeSymbol getSymbol() {
			return symbol;
		}

		private BlockModifierContainerObject register() {
			if (symbol != null) InternalAPI.symbol.registerSymbol(symbol);
			return this;
		}
	}

	public static void initialize() {
		BlockModifierContainerObject.create(WordData.Terrain, 2, Blocks.dirt, 0).register().add(BlockCategory.TERRAIN, 4).add(BlockCategory.STRUCTURE, 1).add(BlockCategory.SOLID, 1);
		BlockModifierContainerObject.create(WordData.Terrain, 2, Blocks.stone, 0).register().add(BlockCategory.TERRAIN, 1).add(BlockCategory.STRUCTURE, 1).add(BlockCategory.SOLID, 1);
		BlockModifierContainerObject.create(WordData.Terrain, 2, Blocks.sandstone, 0).register().add(BlockCategory.TERRAIN, 2).add(BlockCategory.STRUCTURE, 1).add(BlockCategory.SOLID, 1);
		InternalAPI.symbol.registerSymbol(InternalAPI.symbolFact.createSymbol(Blocks.netherrack, 0, WordData.Terrain, 2, new CategoryPair(BlockCategory.TERRAIN, 3), new CategoryPair(BlockCategory.STRUCTURE, 2), new CategoryPair(BlockCategory.SOLID, 2)));
		//BlockModifierContainerObject.create(WordData.Terrain, 2, Blocks.netherrack, 0).register().add(BlockCategory.TERRAIN, 3).add(BlockCategory.STRUCTURE, 2).add(BlockCategory.SOLID, 2);
		BlockModifierContainerObject.create(WordData.Terrain, 3, Blocks.end_stone, 0).register().add(BlockCategory.TERRAIN, 4).add(BlockCategory.STRUCTURE, 3).add(BlockCategory.SOLID, 3);

		BlockModifierContainerObject.create(WordData.Structure, 2, Blocks.nether_brick, 0).register().add(BlockCategory.SOLID, 2).add(BlockCategory.STRUCTURE, 2);
		for (byte i = 0; i < 4; ++i) {
			BlockModifierContainerObject.create(WordData.Structure, 2, Blocks.log, i).register().add(BlockCategory.SOLID, 1).add(BlockCategory.ORGANIC, 1).add(BlockCategory.STRUCTURE, 1);
		}
		for (byte i = 0; i < 2; ++i) {
			BlockModifierContainerObject.create(WordData.Structure, 2, Blocks.log2, i).register().add(BlockCategory.SOLID, 1).add(BlockCategory.ORGANIC, 1).add(BlockCategory.STRUCTURE, 1);
		}

		BlockModifierContainerObject.create(WordData.Ore, 5, Blocks.diamond_ore, 0).register().add(BlockCategory.SOLID, 6).add(BlockCategory.STRUCTURE, 6);
		BlockModifierContainerObject.create(WordData.Ore, 4, Blocks.gold_ore, 0).register().add(BlockCategory.SOLID, 5).add(BlockCategory.STRUCTURE, 5);
		BlockModifierContainerObject.create(WordData.Ore, 3, Blocks.iron_ore, 0).register().add(BlockCategory.SOLID, 4).add(BlockCategory.STRUCTURE, 4);
		BlockModifierContainerObject.create(WordData.Ore, 3, Blocks.coal_ore, 0).register().add(BlockCategory.SOLID, 4).add(BlockCategory.STRUCTURE, 4);
		BlockModifierContainerObject.create(WordData.Ore, 4, Blocks.redstone_ore, 0).register().add(BlockCategory.SOLID, 5).add(BlockCategory.STRUCTURE, 5);

		BlockModifierContainerObject.create(WordData.Chain, 2, Blocks.ice, 0).register().add(BlockCategory.SOLID, 3).add(BlockCategory.FLUID, 3).add(BlockCategory.SEA, 2).add(BlockCategory.STRUCTURE, 3).add(BlockCategory.CRYSTAL, 3);
		BlockModifierContainerObject.create(WordData.Chain, 2, Blocks.packed_ice, 0).register().add(BlockCategory.SOLID, 3).add(BlockCategory.FLUID, 3).add(BlockCategory.TERRAIN, 3).add(BlockCategory.SEA, 3).add(BlockCategory.STRUCTURE, 3).add(BlockCategory.CRYSTAL, 3);
		BlockModifierContainerObject.create(WordData.Chain, 2, Blocks.glass, 0).register().add(BlockCategory.SOLID, 3).add(BlockCategory.STRUCTURE, 3).add(BlockCategory.CRYSTAL, 3);
		BlockModifierContainerObject.create(WordData.Chain, 2, Blocks.snow, 0).register().add(BlockCategory.SOLID, 3).add(BlockCategory.STRUCTURE, 3).add(BlockCategory.CRYSTAL, 3);
		BlockModifierContainerObject.create(WordData.Chain, 3, Blocks.obsidian, 0).register().add(BlockCategory.SOLID, 4).add(BlockCategory.TERRAIN, 4).add(BlockCategory.STRUCTURE, 3).add(BlockCategory.CRYSTAL, 3);
		BlockModifierContainerObject.create(WordData.Chain, 3, Blocks.glowstone, 0).register().add(BlockCategory.SOLID, 4).add(BlockCategory.STRUCTURE, 4).add(BlockCategory.CRYSTAL, 4);
		BlockModifierContainerObject.create(WordData.Chain, 3, Blocks.quartz_ore, 0).register().add(BlockCategory.SOLID, 4).add(BlockCategory.STRUCTURE, 4).add(BlockCategory.CRYSTAL, 4);

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("BlockName", MystObjects.Blocks.crystal);
		//nbt.setInteger("Metadata", 0); (or nbt.setByte would work, too)
		nbt.setString("PoemWord", WordData.Chain);
		nbt.setInteger("Rank", 3);
		ArrayList<Object[]> pairs = new ArrayList<Object[]>();
		pairs.add(new Object[] { "BlockSolid", 4 });
		pairs.add(new Object[] { "BlockStructure", 4 });
		pairs.add(new Object[] { "BlockCrystal", 4 });
		ArrayList<NBTTagCompound> categories = new ArrayList<NBTTagCompound>();
		for (Object[] pair : pairs) {
			NBTTagCompound cat = new NBTTagCompound();
			cat.setString("Category", (String) pair[0]);
			cat.setInteger("Rank", (Integer) pair[1]);
			categories.add(cat);
		}
		nbt.setTag("Categories", NBTUtils.writeTagCompoundCollection(new NBTTagList(), categories));
		FMLInterModComms.sendMessage("Mystcraft", "blockmodifier", nbt);
		//BlockModifierContainerObject.create(WordData.Chain, 3, ModBlocks.crystal, 0).register().add(BlockCategory.SOLID, 4).add(BlockCategory.STRUCTURE, 4).add(BlockCategory.CRYSTAL, 4);

		BlockModifierContainerObject.create(WordData.Sea, 2, Blocks.flowing_water, 0).register().add(BlockCategory.FLUID, 1).add(BlockCategory.SEA, 1);
		BlockModifierContainerObject.create(WordData.Sea, 3, Blocks.flowing_lava, 0).register().add(BlockCategory.FLUID, 2).add(BlockCategory.SEA, 2);

		// color
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.50F, 0.00F, 0.00F, "Maroon")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(1.00F, 0.00F, 0.00F, "Red")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.50F, 0.50F, 0.00F, "Olive")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(1.00F, 1.00F, 0.00F, "Yellow")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 0.50F, 0.00F, "Dark Green")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 1.00F, 0.00F, "Green")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 0.50F, 0.50F, "Teal")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 1.00F, 1.00F, "Cyan")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 0.00F, 0.50F, "Navy")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 0.00F, 1.00F, "Blue")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.50F, 0.00F, 0.50F, "Purple")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(1.00F, 0.00F, 1.00F, "Magenta")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 0.00F, 0.00F, "Black")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.50F, 0.50F, 0.50F, "Grey")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.75F, 0.75F, 0.75F, "Silver")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(1.00F, 1.00F, 1.00F, "White")));
	}
}
